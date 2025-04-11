package pl.ka3wo.swift.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "swiftCodes")
public record SwiftDataBranch(
    @Id String id,
    @Field("address") String address,
    @Field("bankName") String bankName,
    @Field("countryISO2") String countryISO2,
    @Field("isHeadquarter") Boolean isHeadquarter,
    @Field("swiftCode") String swiftCode) {}
