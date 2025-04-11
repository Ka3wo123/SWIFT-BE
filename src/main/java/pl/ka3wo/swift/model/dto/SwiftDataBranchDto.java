package pl.ka3wo.swift.model.dto;

public record SwiftDataBranchDto(
    String address, String bankName, String countryISO2, Boolean isHeadquarter, String swiftCode) {}
