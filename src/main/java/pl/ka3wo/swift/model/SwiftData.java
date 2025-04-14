package pl.ka3wo.swift.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "swiftCodes")
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class SwiftData {
  @Id private String id;

  @JsonProperty("ADDRESS")
  @Field("address")
  private String address;

  @JsonProperty("NAME")
  @Field("bankName")
  private String bankName;

  @JsonProperty("COUNTRY ISO2 CODE")
  @Field("countryISO2")
  private String countryISO2;

  @JsonProperty("COUNTRY NAME")
  @Field("countryName")
  private String countryName;

  @Field("isHeadquarter")
  private Boolean isHeadquarter;

  @JsonProperty("SWIFT CODE")
  @Field("swiftCode")
  private String swiftCode;

  @DocumentReference(collection = "swiftCodes")
  @Field("branches")
  private List<SwiftDataBranch> branches;
}
