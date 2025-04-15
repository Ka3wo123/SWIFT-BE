package pl.ka3wo.swift.integration;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.ka3wo.swift.integration.base.BaseMongoContainer;
import pl.ka3wo.swift.model.SwiftData;
import pl.ka3wo.swift.model.dto.SwiftDataRequest;
import pl.ka3wo.swift.repository.SwiftRepository;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SwiftDataCreationIT extends BaseMongoContainer {
  @LocalServerPort private int port;
  @Autowired private SwiftRepository swiftRepository;
  private final String HEADQUARTER_SWIFT_CODE = "HPGWGBWZXXX";

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
  }

  @Order(1)
  @Test
  public void shouldCreateNewHeadquarterAndNotAssignItself() {
    SwiftDataRequest request =
        new SwiftDataRequest(
            "DIAGON ALLEY, CHARING CROSS ROAD, LONDON, ENGLAND, GB",
            "GRINGOTTS WIZARDING BANK",
            "GB",
            "GREAT BRITAIN",
            true,
            HEADQUARTER_SWIFT_CODE);
    given()
        .accept(ContentType.JSON)
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/v1/swift-codes/")
        .then()
        .statusCode(201)
        .body("message", equalTo("Successfully added new SWIFT data"));

    boolean exists = swiftRepository.existsBySwiftCode(request.swiftCode());
    assertTrue(exists);

    Optional<SwiftData> hq = swiftRepository.findBySwiftCode(HEADQUARTER_SWIFT_CODE);
    assertTrue(hq.isPresent());
    boolean isSelfAssigned =
        hq.get().getBranches().stream().anyMatch(b -> request.swiftCode().equals(b.swiftCode()));
    assertFalse(isSelfAssigned);
  }

  @Order(2)
  @Test
  public void shouldCreateNewBranchAndAssociateWithHeadquarter() {
    SwiftDataRequest request =
        new SwiftDataRequest(
            "LONDON, ENGLAND, GB",
            "GRINGOTTS WIZARDING BANK",
            "GB",
            "GREAT BRITAIN",
            false,
            "HPGWGBWZABC");
    given()
        .accept(ContentType.JSON)
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/v1/swift-codes/")
        .then()
        .statusCode(201)
        .body("message", equalTo("Successfully added new SWIFT data"));

    boolean exists = swiftRepository.existsBySwiftCode(request.swiftCode());
    assertTrue(exists);

    Optional<SwiftData> hq = swiftRepository.findBySwiftCode(HEADQUARTER_SWIFT_CODE);
    assertTrue(hq.isPresent());
    boolean isBranchAssigned =
        hq.get().getBranches().stream().anyMatch(b -> request.swiftCode().equals(b.swiftCode()));
    assertTrue(isBranchAssigned);
  }

  @Test
  public void shouldThrowConflictException() {
    SwiftDataRequest request =
        new SwiftDataRequest(
            "DIAGON ALLEY, CHARING CROSS ROAD, LONDON, ENGLAND, GB",
            "GRINGOTTS WIZARDING BANK",
            "GB",
            "GREAT BRITAIN",
            true,
            "HPGWGBWZXXX");
    given()
        .accept(ContentType.JSON)
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/v1/swift-codes/")
        .then()
        .statusCode(409)
        .body("title", equalTo("SWIFT code conflict"))
        .body("status", equalTo(409))
        .body("detail", equalTo("Data with SWIFT code " + request.swiftCode() + " already exists"))
        .body("instance", equalTo("/v1/swift-codes/"));
  }

  @Test
  public void shouldThrowBadRequestException() {
    SwiftDataRequest request =
        new SwiftDataRequest(
            null, "GRINGOTTS WIZARDING BANK", "GB", "GREAT BRITAIN", true, "HPGWGBWZXXX");
    given()
        .accept(ContentType.JSON)
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/v1/swift-codes/")
        .then()
        .statusCode(400)
        .body("title", equalTo("Invalid SWIFT data"))
        .body("status", equalTo(400))
        .body("errors", not(emptyArray()))
        .body("errors.address.message", equalTo("must not be null"))
        .body("errors.address.rejectedValue", is(nullValue()));
  }
}
