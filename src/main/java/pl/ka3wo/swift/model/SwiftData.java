package pl.ka3wo.swift.model;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "swift_codes")
public record SwiftData(
    @Id String id,
    @Field("address") String address,
    @Field("bank_name") String bankName,
    @Field("country_ISO2") String countryISO2,
    @Field("country_name") String countryName,
    @Field("is_headquarter") Boolean isHeadquarter,
    @Field("swift_code") String swiftCode,
    List<SwiftDataBranch> branches) {}
