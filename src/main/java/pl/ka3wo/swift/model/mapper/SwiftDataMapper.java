package pl.ka3wo.swift.model.mapper;

import com.fasterxml.jackson.annotation.JsonView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ka3wo.swift.model.SwiftData;
import pl.ka3wo.swift.model.SwiftDataBranch;
import pl.ka3wo.swift.model.dto.SwiftDataDto;
import pl.ka3wo.swift.model.dto.SwiftDataRequest;

@Mapper(componentModel = "spring")
public interface SwiftDataMapper {
  @Mapping(source = "swiftCode", target = "swiftCode")
  @Mapping(source = "address", target = "address")
  @Mapping(source = "bankName", target = "bankName")
  @Mapping(source = "countryISO2", target = "countryISO2")
  @Mapping(source = "countryName", target = "countryName")
  @Mapping(source = "isHeadquarter", target = "isHeadquarter")
  @Mapping(source = "branches", target = "branches")
  SwiftDataDto toSwiftDataDto(SwiftData entity);

  @Mapping(source = "swiftCode", target = "swiftCode")
  @Mapping(source = "address", target = "address")
  @Mapping(source = "bankName", target = "bankName")
  @Mapping(source = "countryISO2", target = "countryISO2")
  @Mapping(source = "countryName", target = "countryName")
  @Mapping(source = "isHeadquarter", target = "isHeadquarter")
  @Mapping(source = "branches", target = "branches")
  SwiftData toSwiftDataEntity(SwiftDataDto dto);

  @Mapping(source = "swiftCode", target = "swiftCode")
  @Mapping(source = "address", target = "address")
  @Mapping(source = "bankName", target = "bankName")
  @Mapping(source = "countryISO2", target = "countryISO2")
  @Mapping(source = "countryName", target = "countryName")
  @Mapping(target = "isHeadquarter", expression = "java(request.swiftCode().endsWith(\"XXX\"))")
  SwiftData fromSwiftDataRequest(SwiftDataRequest request);

  @Mapping(source = "swiftCode", target = "swiftCode")
  @Mapping(source = "address", target = "address")
  @Mapping(source = "bankName", target = "bankName")
  @Mapping(source = "countryISO2", target = "countryISO2")
  @Mapping(target = "isHeadquarter", constant = "false")
  SwiftDataBranch toSwiftDataBranch(SwiftData entity);


}
