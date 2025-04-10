package pl.ka3wo.swift.model;

public record SwiftDataBranch(
    String address, String bankName, String countryISO2, Boolean isHeadquarter, String swiftCode) {}
