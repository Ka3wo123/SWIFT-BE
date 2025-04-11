package pl.ka3wo.swift.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import pl.ka3wo.swift.exception.DuplicateSwiftCodeException;
import pl.ka3wo.swift.exception.NoSwiftDataFound;
import pl.ka3wo.swift.model.SwiftData;
import pl.ka3wo.swift.model.dto.ApiResponse;
import pl.ka3wo.swift.model.dto.SwiftDataBranchDto;
import pl.ka3wo.swift.model.dto.SwiftDataCountryDto;
import pl.ka3wo.swift.model.dto.SwiftDataDto;
import pl.ka3wo.swift.model.dto.SwiftDataRequest;
import pl.ka3wo.swift.repoistory.SwiftRepository;

@Service
public class SwiftService {
  private final SwiftRepository swiftRepository;

  public SwiftService(SwiftRepository swiftRepository) {
    this.swiftRepository = swiftRepository;
  }

  public List<SwiftDataDto> getAll() {
    return swiftRepository.findAll().stream().map(this::toDto).toList();
  }

  public SwiftDataDto getBySwiftCode(String swiftCode) {
    SwiftData swiftData =
        swiftRepository
            .findBySwiftCode(swiftCode)
            .orElseThrow(() -> new NoSwiftDataFound("SWIFT data not found"));
    return toDto(swiftData);
  }

  public SwiftDataCountryDto getByCountryISO2code(String countryISO2code) {
    List<SwiftData> swiftDataList = swiftRepository.findByCountryISO2(countryISO2code);

    if (swiftDataList.isEmpty()) {
      return new SwiftDataCountryDto(countryISO2code, null, List.of());
    }

    List<SwiftDataDto> swiftDataDtos = swiftDataList.stream().map(this::toDto).toList();
    String countryName = swiftDataDtos.get(0).countryName();

    return new SwiftDataCountryDto(countryISO2code, countryName, swiftDataDtos);
  }

  public ApiResponse save(SwiftDataRequest swiftData) {
    boolean swiftDataExists = swiftRepository.existsBySwiftCode(swiftData.swiftCode());

    if (swiftDataExists) {
      throw new DuplicateSwiftCodeException("Data with provided SWIFT code already exists");
    }
    SwiftData entity = toEntity(swiftData);
    swiftRepository.save(entity);
    return new ApiResponse("Added new SWIFT data");
  }

  public ApiResponse deleteOneBySwiftCode(String swiftCode) {
    boolean swiftDataExists = swiftRepository.existsBySwiftCode(swiftCode);

    if (!swiftDataExists) {
      throw new NoSwiftDataFound("SWIFT data not found");
    }

    swiftRepository.deleteBySwiftCode(swiftCode);
    return new ApiResponse("Deleted SWIFT data for: " + swiftCode);
  }

  private SwiftDataDto toDto(SwiftData swiftData) {
    List<SwiftDataBranchDto> branchDtos =
        swiftData.getBranches().stream()
            .map(
                branch ->
                    new SwiftDataBranchDto(
                        branch.address(),
                        branch.bankName(),
                        branch.countryISO2(),
                        branch.isHeadquarter(),
                        branch.swiftCode()))
            .collect(Collectors.toList());

    return new SwiftDataDto(
        swiftData.getAddress(),
        swiftData.getBankName(),
        swiftData.getCountryISO2(),
        swiftData.getCountryName(),
        swiftData.getIsHeadquarter(),
        swiftData.getSwiftCode(),
        branchDtos);
  }

  private SwiftData toEntity(SwiftDataRequest dataRequest) {
    return new SwiftData(
        null,
        dataRequest.address(),
        dataRequest.bankName(),
        dataRequest.countryISO2(),
        dataRequest.countryName(),
        dataRequest.isHeadquarter(),
        dataRequest.swiftCode(),
       null);
  }
}
