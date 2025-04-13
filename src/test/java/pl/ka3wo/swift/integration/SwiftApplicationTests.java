package pl.ka3wo.swift.integration;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SwiftApplicationTests {
  @LocalServerPort private int port;

  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0.6");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @BeforeAll
  static void beforeAll() {
    mongoDBContainer.start();
  }

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
  }

  @Test
  public void shouldGetOneSwiftCode() {
    String swiftCode = "AAISALTRXXX";
    given()
        .accept(ContentType.JSON)
        .when()
        .get("/v1/swift-codes/{swiftCode}", swiftCode)
        .then()
        .statusCode(200)
        .body("swiftCode", equalTo(swiftCode))
        .body("bankName", notNullValue());
  }

  @Test
  public void shouldDeleteOneRecordBySwiftCode() {
    String swiftCode = "AAISALTRXXX";
    given()
        .accept(ContentType.JSON)
        .when()
        .delete("/v1/swift-codes/{swiftCode}", swiftCode)
        .then()
        .statusCode(200)
        .body(
            "message",
            equalTo(String.format("Successfully deleted SWIFT data for code: %s", swiftCode)));
  }

  @Test
  public void shouldReturnNotFound() {
    String swiftCode = "AAISALTRXXX";
    given()
        .accept(ContentType.JSON)
        .when()
        .get("/v1/swift-codes/{swiftCode}", swiftCode)
        .then()
        .statusCode(404)
        .body("title", equalTo("SWIFT data not found"))
        .body("status", equalTo(404))
        .body("detail", equalTo("SWIFT data with SWIFT code " + swiftCode + " not found"))
        .body("instance", equalTo("/v1/swift-codes/" + swiftCode));
  }

  @ParameterizedTest
  @CsvSource({
          "NONEXIST1, /v1/swift-codes/NONEXIST1",
          "FAKESWFT2, /v1/swift-codes/FAKESWFT2"
  })
  void shouldReturnNotFound(String swiftCode, String expectedInstance) {
    given()
            .accept(ContentType.JSON)
            .when()
            .get("/v1/swift-codes/{swiftCode}", swiftCode)
            .then()
            .statusCode(404)
            .body("title", equalTo("SWIFT data not found"))
            .body("status", equalTo(404))
            .body("instance", equalTo(expectedInstance));
  }


}
