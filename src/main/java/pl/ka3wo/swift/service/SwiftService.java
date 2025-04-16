package pl.ka3wo.swift.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import pl.ka3wo.swift.exception.DuplicateSwiftCodeException;
import pl.ka3wo.swift.exception.NoSwiftDataFound;
import pl.ka3wo.swift.model.SwiftData;
import pl.ka3wo.swift.model.SwiftDataBranch;
import pl.ka3wo.swift.model.dto.*;
import pl.ka3wo.swift.model.mapper.SwiftDataMapper;
import pl.ka3wo.swift.repository.SwiftRepository;

@Service
public class SwiftService {
  private final SwiftRepository swiftRepository;
  private final SwiftDataMapper swiftDataMapper;

  public SwiftService(SwiftRepository swiftRepository, SwiftDataMapper swiftDataMapper) {
    this.swiftRepository = swiftRepository;
    this.swiftDataMapper = swiftDataMapper;
  }

  public SwiftDataDto getBySwiftCode(String swiftCode) {
    return swiftRepository
        .findBySwiftCode(swiftCode)
        .map(swiftDataMapper::toSwiftDataDto)
        .orElseThrow(
            () ->
                new NoSwiftDataFound(
                    String.format("SWIFT data with SWIFT code %s not found", swiftCode)));
  }

  public SwiftDataCountryDto getByCountryISO2code(String countryISO2code) {
    List<SwiftData> swiftDataList = swiftRepository.findByCountryISO2(countryISO2code);

    if (swiftDataList.isEmpty()) {
      throw new NoSwiftDataFound(
          String.format("SWIFT data with country ISO2 code %s not found", countryISO2code));
    }

    List<SwiftDataDto> swiftDataDtos =
        swiftDataList.stream().map(swiftDataMapper::toSwiftDataDto).toList();
    String countryName = String.valueOf(swiftDataDtos.stream().findFirst());

    return new SwiftDataCountryDto(countryISO2code, countryName, swiftDataDtos);
  }

  public ApiResponse create(SwiftDataRequest swiftDataRequest) {
    boolean swiftDataExists = swiftRepository.existsBySwiftCode(swiftDataRequest.swiftCode());

    if (swiftDataExists) {
      throw new DuplicateSwiftCodeException(
          String.format("Data with SWIFT code %s already exists", swiftDataRequest.swiftCode()));
    }
    String headquarterSuffix = "XXX";

    SwiftData entity = swiftDataMapper.fromSwiftDataRequest(swiftDataRequest);
    String prefix = entity.getSwiftCode().substring(0, 8);

    if (Boolean.TRUE.equals(
        entity.getIsHeadquarter() || entity.getSwiftCode().endsWith(headquarterSuffix))) {
      createHeadquarter(entity, prefix);
    } else {
      createBranch(entity, prefix);
    }

    return new CreateSwiftDataResponse();
  }

  public ApiResponse deleteOneBySwiftCode(String swiftCode) {
    boolean swiftDataExists = swiftRepository.existsBySwiftCode(swiftCode);

    if (!swiftDataExists) {
      throw new NoSwiftDataFound(
          String.format("SWIFT data with SWIFT code %s not found", swiftCode));
    }

    swiftRepository.deleteBySwiftCode(swiftCode);
    return new DeleteSwiftDataResponse();
  }

  private void createHeadquarter(SwiftData entity, String prefix) {
    List<SwiftDataBranch> branches =
        swiftRepository.findBySwiftCodeStartingWith(prefix).stream()
            .filter(sd -> !sd.getSwiftCode().equals(entity.getSwiftCode()))
            .filter(sd -> !Boolean.TRUE.equals(sd.getIsHeadquarter()))
            .map(swiftDataMapper::toSwiftDataBranch)
            .toList();

    entity.setBranches(branches);
    entityToUpperCase(entity);
    swiftRepository.save(entity);
  }

  private void createBranch(SwiftData entity, String prefix) {
    entityToUpperCase(entity);
    SwiftData saved = swiftRepository.save(entity);

    swiftRepository
        .findBySwiftCode(prefix + "XXX")
        .ifPresent(
            hq -> {
              List<SwiftDataBranch> branches = hq.getBranches();

              SwiftDataBranch swiftDataBranch =
                  new SwiftDataBranch(
                      saved.getId(),
                      saved.getAddress().toUpperCase(),
                      saved.getBankName().toUpperCase(),
                      saved.getCountryISO2().toUpperCase(),
                      false,
                      saved.getSwiftCode().toUpperCase());

              branches.add(swiftDataBranch);
              hq.setBranches(branches);
              swiftRepository.save(hq);
            });
  }

  private void entityToUpperCase(SwiftData entity) {
    entity.setAddress(entity.getAddress().toUpperCase());
    entity.setSwiftCode(entity.getSwiftCode().toUpperCase());
    entity.setBankName(entity.getBankName().toUpperCase());
    entity.setCountryISO2(entity.getCountryISO2().toUpperCase());
    entity.setCountryName(entity.getCountryName().toUpperCase());
  }
}
