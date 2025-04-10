package pl.ka3wo.swift.model.dto;


import java.util.List;

public record SwiftDataCountryDto(
    String countryISO2, String countryName, List<SwiftDataDto> swiftCodes) {}
