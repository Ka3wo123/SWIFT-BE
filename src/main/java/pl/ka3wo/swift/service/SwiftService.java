package pl.ka3wo.swift.service;

import java.util.List;
import org.springframework.stereotype.Service;
import pl.ka3wo.swift.exception.DuplicateSwiftCodeException;
import pl.ka3wo.swift.exception.NoSwiftDataFound;
import pl.ka3wo.swift.model.SwiftData;
import pl.ka3wo.swift.model.SwiftDataBranch;
import pl.ka3wo.swift.model.dto.ApiResponse;
import pl.ka3wo.swift.model.dto.SwiftDataCountryDto;
import pl.ka3wo.swift.model.dto.SwiftDataDto;
import pl.ka3wo.swift.model.dto.SwiftDataRequest;
import pl.ka3wo.swift.model.mapper.SwiftDataBranchMapper;
import pl.ka3wo.swift.model.mapper.SwiftDataMapper;
import pl.ka3wo.swift.repository.SwiftRepository;

@Service
public class SwiftService {
  private final SwiftRepository swiftRepository;
  private final SwiftDataMapper swiftDataMapper;
  private final SwiftDataBranchMapper branchMapper;

  public SwiftService(
      SwiftRepository swiftRepository,
      SwiftDataMapper swiftDataMapper,
      SwiftDataBranchMapper branchMapper) {
    this.swiftRepository = swiftRepository;
    this.swiftDataMapper = swiftDataMapper;
    this.branchMapper = branchMapper;
  }

  public List<SwiftDataDto> getAll() {
    return swiftRepository.findAll().stream().map(swiftDataMapper).toList();
  }

  public SwiftDataDto getBySwiftCode(String swiftCode) {
    return swiftRepository
        .findBySwiftCode(swiftCode)
        .map(swiftDataMapper)
        .orElseThrow(() -> new NoSwiftDataFound(String.format("SWIFT data with SWIFT code %s not found", swiftCode)));
  }

  public SwiftDataCountryDto getByCountryISO2code(String countryISO2code) {
    List<SwiftData> swiftDataList = swiftRepository.findByCountryISO2(countryISO2code);

    if (swiftDataList.isEmpty()) {
      throw new NoSwiftDataFound(String.format("SWIFT data with country ISO2 code %s not found", countryISO2code));
    }

    List<SwiftDataDto> swiftDataDtos = swiftDataList.stream().map(swiftDataMapper).toList();
    String countryName = swiftDataDtos.get(0).countryName();

    return new SwiftDataCountryDto(countryISO2code, countryName, swiftDataDtos);
  }

  public ApiResponse create(SwiftDataRequest swiftData) {
    boolean swiftDataExists = swiftRepository.existsBySwiftCode(swiftData.swiftCode());

    if (swiftDataExists) {
      throw new DuplicateSwiftCodeException(String.format("Data with SWIFT code %s already exists", swiftData.swiftCode()));
    }

    SwiftData entity = toEntity(swiftData);
    String prefix = entity.getSwiftCode().substring(0, 8);

    if (entity.getIsHeadquarter()) {
      List<SwiftDataBranch> branches =
          swiftRepository.findBySwiftCodeStartingWith(prefix).stream()
              .filter(sd -> !sd.getSwiftCode().equals(entity.getSwiftCode()))
              .filter(sd -> !sd.getIsHeadquarter())
              .map(
                  sd ->
                      new SwiftDataBranch(
                          sd.getId(),
                          sd.getAddress(),
                          sd.getBankName(),
                          sd.getCountryISO2(),
                          false,
                          sd.getSwiftCode()))
              .toList();
      entity.setBranches(branches);
      swiftRepository.save(entity);
    } else {
      SwiftData saved = swiftRepository.save(entity);
      swiftRepository
          .findBySwiftCode(prefix + "XXX")
          .ifPresent(
              hq -> {
                List<SwiftDataBranch> branches = hq.getBranches();

                SwiftDataBranch swiftDataBranch =
                    new SwiftDataBranch(
                        saved.getId(),
                        saved.getAddress(),
                        saved.getBankName(),
                        saved.getCountryISO2(),
                        false,
                        saved.getSwiftCode());

                branches.add(swiftDataBranch);
                hq.setBranches(branches);
                swiftRepository.save(hq);
              });
    }

    return new ApiResponse("Successfully added new SWIFT data");
  }

  public ApiResponse deleteOneBySwiftCode(String swiftCode) {
    boolean swiftDataExists = swiftRepository.existsBySwiftCode(swiftCode);

    if (!swiftDataExists) {
      throw new NoSwiftDataFound("SWIFT data not found");
    }

    swiftRepository.deleteBySwiftCode(swiftCode);
    return new ApiResponse("Successfully deleted SWIFT data for code: " + swiftCode);
  }

  private SwiftData toEntity(SwiftDataRequest dataRequest) {
    boolean isHeadquarter = dataRequest.swiftCode().endsWith("XXX");
    return new SwiftData(
        null,
        dataRequest.address(),
        dataRequest.bankName(),
        dataRequest.countryISO2(),
        dataRequest.countryName(),
        isHeadquarter,
        dataRequest.swiftCode(),
        null);
  }
}
