package pl.ka3wo.swift.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ka3wo.swift.model.SwiftData;
import pl.ka3wo.swift.model.SwiftDataBranch;
import pl.ka3wo.swift.model.dto.SwiftDataDto;
import pl.ka3wo.swift.model.dto.SwiftDataRequest;

@Mapper(componentModel = "spring")
public interface SwiftDataMapper {

  SwiftDataDto toSwiftDataDto(SwiftData entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "branches", ignore = true)
  SwiftData fromSwiftDataRequest(SwiftDataRequest request);

  SwiftDataBranch toSwiftDataBranch(SwiftData entity);
}
