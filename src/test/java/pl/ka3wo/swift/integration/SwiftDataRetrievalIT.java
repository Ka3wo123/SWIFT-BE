package pl.ka3wo.swift.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.ka3wo.swift.integration.base.BaseMongoContainer;

public class SwiftDataRetrievalIT extends BaseMongoContainer {

  @LocalServerPort private int port;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
  }

  @Test
  public void shouldGetOneBySwiftCode() {
    String swiftCode = "AAAAGBCVAAA";
    given()
        .when()
        .get("/v1/swift-codes/{swiftCode}", swiftCode)
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("address", notNullValue(String.class))
        .body("bankName", notNullValue(String.class))
        .body("countryISO2", notNullValue(String.class))
        .body("countryName", notNullValue(String.class))
        .body("isHeadquarter", notNullValue(Boolean.class))
        .body("swiftCode", equalTo(swiftCode))
        .body("branches", is(not(emptyArray())));
  }

  @Test
  public void shouldReturnByCountryISO2Code() {
    String countryISO2code = "GB";
    given()
        .when()
        .get("/v1/swift-codes/country/{countryISO2code}", countryISO2code)
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("countryISO2", equalTo(countryISO2code))
        .body("countryName", notNullValue())
        .body("swiftCodes", not(emptyArray()))
        .body("swiftCodes.address", notNullValue(String.class))
        .body("swiftCodes.bankName", notNullValue(String.class))
        .body("swiftCodes.countryISO2", notNullValue(String.class))
        .body("swiftCodes.countryName", notNullValue(String.class))
        .body("swiftCodes.isHeadquarter", notNullValue(Boolean.class))
        .body("swiftCodes.swiftCode", notNullValue(String.class))
        .body("swiftCodes.branches", anyOf(is(emptyArray()), is(not(emptyArray())), is(nullValue())));
  }

  @Test
  public void shouldThrowNotFoundBySwiftCode() {
    String swiftCode = "XXXXXXXX";
    given()
            .when()
            .get("/v1/swift-codes/{swiftCode}", swiftCode)
            .then()
            .statusCode(404)
            .body("title", equalTo("SWIFT data not found"))
            .body("status", equalTo(404))
            .body("detail", equalTo("SWIFT data with SWIFT code " + swiftCode + " not found"))
            .body("instance", equalTo("/v1/swift-codes/" + swiftCode));
  }

  @Test
  public void shouldThrowNotFoundByCountryISO2Code() {
    String countryISO2Code = "--";
    given()
            .when()
            .get("/v1/swift-codes/country/{countryISO2Code}", countryISO2Code)
            .then()
            .statusCode(404)
            .body("title", equalTo("SWIFT data not found"))
            .body("status", equalTo(404))
            .body("detail", equalTo("SWIFT data with country ISO2 code " + countryISO2Code + " not found"))
            .body("instance", equalTo("/v1/swift-codes/country/" + countryISO2Code));
  }
}
