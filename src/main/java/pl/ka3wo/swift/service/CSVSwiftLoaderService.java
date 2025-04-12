package pl.ka3wo.swift.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import pl.ka3wo.swift.model.SwiftData;
import pl.ka3wo.swift.model.SwiftDataBranch;
import pl.ka3wo.swift.repository.SwiftRepository;

@Service
public class CSVSwiftLoaderService {
  private static final Logger LOGGER = LoggerFactory.getLogger(CSVSwiftLoaderService.class);
  private final SwiftRepository swiftRepository;

  public CSVSwiftLoaderService(SwiftRepository swiftRepository) {
    this.swiftRepository = swiftRepository;
  }

  public void extractAndLoad() {
    String headquarterSuffix = "XXX";
    String swiftCodesFile = "swiftcodes.csv";

    try {
      long count = swiftRepository.count();
      if (count > 0) {
        LOGGER.info("Swift codes already loaded. Skipping...");
        return;
      }
      CsvMapper csvMapper = new CsvMapper();
      CsvSchema schema = CsvSchema.emptySchema().withHeader();

      InputStream csvStream = new ClassPathResource(swiftCodesFile).getInputStream();
      MappingIterator<SwiftData> it =
          csvMapper.readerFor(SwiftData.class).with(schema).readValues(csvStream);
      List<SwiftData> swiftData = it.readAll();

      for (SwiftData sd : swiftData) {
        sd.setIsHeadquarter(sd.getSwiftCode().endsWith(headquarterSuffix));
      }

      swiftRepository.saveAll(swiftData);

      LOGGER.info("Successfully loaded {} documents to database", swiftData.size());

      Map<String, List<SwiftData>> groupedByPrefix =
          swiftData.stream()
              .collect(Collectors.groupingBy(sd -> sd.getSwiftCode().substring(0, 8)));

      for (List<SwiftData> group : groupedByPrefix.values()) {
        SwiftData headquarter =
            group.stream().filter(SwiftData::getIsHeadquarter).findFirst().orElse(null);
        if (headquarter != null) {
          List<SwiftDataBranch> branches =
              group.stream()
                  .filter(sd -> !sd.equals(headquarter))
                  .map(
                      sd ->
                          new SwiftDataBranch(
                              sd.getId(),
                              sd.getAddress(),
                              sd.getBankName(),
                              sd.getCountryISO2(),
                              sd.getIsHeadquarter(),
                              sd.getSwiftCode()))
                  .toList();
          headquarter.setBranches(branches);
          swiftRepository.save(headquarter);
        }
      }

      LOGGER.info(
          "Assigned branches to appropriate headquarters {}",
          swiftData.stream().filter(SwiftData::getIsHeadquarter).toList().size());

    } catch (Exception e) {
      LOGGER.error("Error while extracting data: {}", String.valueOf(e));
    }
  }
}
