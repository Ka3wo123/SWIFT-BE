package pl.ka3wo.swift.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.ka3wo.swift.integration.base.BaseMongoContainer;
import pl.ka3wo.swift.repository.SwiftRepository;

public class SwiftDataDeletionIT extends BaseMongoContainer {
    @LocalServerPort
    private int port;

    @Autowired private SwiftRepository swiftRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void shouldDeleteOneAndReturnConfirmationMessage() {
        String swiftCode = "AAAAGBCV123";
        given()
                .accept(ContentType.JSON)
                .when()
                .delete("/v1/swift-codes/{swiftCode}", swiftCode)
                .then()
                .statusCode(200)
                .body("message", equalTo("Successfully deleted SWIFT data"));
        boolean exists = swiftRepository.existsBySwiftCode(swiftCode);
        assertFalse(exists);
    }

    @Test
    public void shouldDeleteHeadquarterAndNotDeleteAssociatedBranches() {
        String swiftCode = "AAAAGBCVXXX";
        given()
                .accept(ContentType.JSON)
                .when()
                .delete("/v1/swift-codes/{swiftCode}", swiftCode)
                .then()
                .statusCode(200)
                .body("message", equalTo("Successfully deleted SWIFT data"));
        boolean exists = swiftRepository.existsBySwiftCode(swiftCode);
        assertFalse(exists);

        exists = swiftRepository.existsBySwiftCode("AAAAGBCVAAA");
        assertTrue(exists);
    }

    @Test
    public void shouldThrowNotFoundException() {
        String swiftCode = "XXXXXXXX";
        given()
                .accept(ContentType.JSON)
                .when()
                .delete("/v1/swift-codes/{swiftCode}", swiftCode)
                .then()
                .statusCode(404)
                .body("title", equalTo("SWIFT data not found"))
                .body("status", equalTo(404))
                .body("detail", equalTo("SWIFT data with SWIFT code " + swiftCode + " not found"))
                .body("instance", equalTo("/v1/swift-codes/" + swiftCode));
    }
    
}
