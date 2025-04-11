package pl.ka3wo.swift.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SwiftDataRequest(
    @NotNull(message = "must not be null") String address,
    @NotNull(message = "must not be null") @NotBlank(message = "must not be blank") String bankName,
    @NotNull(message = "must not be null") @NotBlank(message = "must not be blank")
        String countryISO2,
    @NotNull(message = "must not be null") @NotBlank(message = "must not be blank")
        String countryName,
    Boolean isHeadquarter,
    @NotNull(message = "must not be null")
        @NotBlank(message = "must not be blank")
        @Pattern(
            regexp = "^[A-Z0-9]{4}[A-Z]{2}[A-Z0-9]{2}(XXX|[A-Z0-9]{3})?$",
            message = "must be valid code")
        String swiftCode) {}
