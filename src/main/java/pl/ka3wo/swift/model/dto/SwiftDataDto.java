package pl.ka3wo.swift.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SwiftDataDto(
    String address,
    String bankName,
    String countryISO2,
    String countryName,
    Boolean isHeadquarter,
    String swiftCode,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<SwiftDataBranchDto> branches) {}
