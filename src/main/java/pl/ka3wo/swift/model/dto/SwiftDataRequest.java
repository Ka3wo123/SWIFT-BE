package pl.ka3wo.swift.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SwiftDataRequest(
    @NotNull String address,
    @NotNull @NotBlank String bankName,
    @NotNull @NotBlank String countryISO2,
    @NotNull @NotBlank String countryName,
    @NotNull Boolean isHeadquarter,
    @NotNull @Pattern(regexp = "^[A-Z0-9]{4}[A-Z]{2}[A-Z0-9]{2}(XXX|[A-Z0-9]{3})?$")
        String swiftCode) {}
