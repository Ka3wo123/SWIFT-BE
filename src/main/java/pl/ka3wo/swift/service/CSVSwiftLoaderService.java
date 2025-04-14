package pl.ka3wo.swift.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import pl.ka3wo.swift.model.SwiftData;
import pl.ka3wo.swift.model.SwiftDataBranch;
import pl.ka3wo.swift.repository.SwiftRepository;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CSVSwiftLoaderService {
  private static final Logger LOGGER = LoggerFactory.getLogger(CSVSwiftLoaderService.class);
  private final SwiftRepository swiftRepository;
  private static final String SWIFT_CODES_FILE = "swiftcodes.csv";
  private static final String HEADQUARTER_SUFFIX = "XXX";

  public CSVSwiftLoaderService(SwiftRepository swiftRepository) {
    this.swiftRepository = swiftRepository;
  }

  public void loadSwiftDataFromCsv() {
    try {
      List<SwiftData> swiftDataList = readCsv();
      upsertSwiftRecords(swiftDataList);
      assignBranchesToHeadquarters(swiftDataList);
    } catch (Exception e) {
      LOGGER.error("Error while extracting data: {}", e.getMessage(), e);
    }
  }

  private List<SwiftData> readCsv() throws Exception {
    CsvMapper csvMapper = new CsvMapper();
    CsvSchema schema = CsvSchema.emptySchema().withHeader();

    InputStream csvStream = new ClassPathResource(SWIFT_CODES_FILE).getInputStream();
    MappingIterator<SwiftData> it = csvMapper.readerFor(SwiftData.class).with(schema).readValues(csvStream);

    List<SwiftData> swiftDataList = it.readAll();

    swiftDataList.forEach(sd -> sd.setIsHeadquarter(sd.getSwiftCode().endsWith(HEADQUARTER_SUFFIX)));

    return swiftDataList;
  }

  private void upsertSwiftRecords(List<SwiftData> swiftDataList) {
    for (SwiftData sd : swiftDataList) {
      swiftRepository.findBySwiftCode(sd.getSwiftCode()).ifPresentOrElse(
              existing -> {
                sd.setId(existing.getId());
                swiftRepository.save(sd);
              },
              () -> swiftRepository.save(sd)
      );
    }

    swiftRepository.saveAll(swiftDataList);
    LOGGER.info("Successfully upserted {} documents to database", swiftDataList.size());
  }

  private void assignBranchesToHeadquarters(List<SwiftData> swiftDataList) {
    Map<String, List<SwiftData>> groupedByPrefix =
            swiftDataList.stream()
                    .collect(Collectors.groupingBy(sd -> sd.getSwiftCode().substring(0, 8)));

    for (List<SwiftData> group : groupedByPrefix.values()) {
      SwiftData headquarter = group.stream()
              .filter(SwiftData::getIsHeadquarter)
              .findFirst()
              .orElse(null);

      if (headquarter != null) {
        List<SwiftDataBranch> branches = group.stream()
                .filter(sd -> !sd.equals(headquarter))
                .map(sd -> new SwiftDataBranch(
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

    long hqCount = swiftDataList.stream().filter(SwiftData::getIsHeadquarter).count();
    LOGGER.info("Assigned branches to {} headquarters", hqCount);
  }
}
