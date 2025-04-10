package pl.ka3wo.swift.model.dto;

import java.util.List;
import pl.ka3wo.swift.model.SwiftDataBranch;

public record SwiftDataDto(
    String address,
    String bankName,
    String countryISO2,
    String countryName,
    Boolean isHeadquarter,
    String swiftCode,
    List<SwiftDataBranch> branches) {}
