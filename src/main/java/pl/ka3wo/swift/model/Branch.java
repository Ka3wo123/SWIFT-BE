package pl.ka3wo.swift.model;

public record Branch(
    String address,
    String bankName,
    String countryISO2,
    String countryName,
    Boolean isHeadquarter,
    String swiftCode) {}
