package pl.ka3wo.swift.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SwiftDataRequest(
    @NotNull(message = "address cannot be null") @NotBlank(message = "address cannot be blank")
        String address,
    @NotNull(message = "bankName cannot be null") @NotBlank(message = "bankName cannot be blank")
        String bankName,
    @NotNull(message = "countryISO2 cannot be null") @NotBlank(message = "countryISO2 cannot be blank")
        String countryISO2,
    @NotNull(message = "countryName cannot be null") @NotBlank(message = "countryName cannot be blank")
        String countryName,
    Boolean isHeadquarter,
    @NotNull(message = "swiftCode cannot be null") @NotBlank(message = "swiftCode cannot be blank")
        String swiftCode) {}
