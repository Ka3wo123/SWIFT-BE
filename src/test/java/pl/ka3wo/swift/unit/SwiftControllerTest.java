package pl.ka3wo.swift.unit;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.ka3wo.swift.controller.SwiftController;
import pl.ka3wo.swift.model.dto.ApiResponse;
import pl.ka3wo.swift.model.dto.SwiftDataRequest;
import pl.ka3wo.swift.service.SwiftService;

@WebMvcTest(SwiftController.class)
public class SwiftControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockitoBean private SwiftService service;

  @Autowired private ObjectMapper objectMapper;

  static Stream<String> invalidSwiftCodes() {
    return Stream.of(
        null,
        "",
        "1231212",
        "AAAAAAAABBBBBBBBBCCCCCCC",
        "abababab",
        "ABCDABABXXX00000",
        "ABCDABAB00000",
        "ABC",
        "AB*CAD10");
  }

  static Stream<Arguments> invalidRequests() {
    return Stream.of(
        Arguments.of(
            new SwiftDataRequest(
                null, "GRINGOTTS WIZARDING BANK", "GB", "GREAT BRITAIN", true, "HPGWGBWZXXX"),
            "address"),
        Arguments.of(
            new SwiftDataRequest(
                "DIAGON ALLEY, CHARING CROSS ROAD, LONDON, ENGLAND, GB",
                null,
                "GB",
                "GREAT BRITAIN",
                false,
                "HPGWGBWZXXX"),
            "bankName"),
        Arguments.of(
            new SwiftDataRequest(
                "DIAGON ALLEY, CHARING CROSS ROAD, LONDON, ENGLAND, GB",
                "",
                "GB",
                "GREAT BRITAIN",
                false,
                "HPGWGBWZXXX"),
            "bankName"),
        Arguments.of(
            new SwiftDataRequest(
                "DIAGON ALLEY, CHARING CROSS ROAD, LONDON, ENGLAND, GB",
                "GRINGOTTS WIZARDING BANK",
                null,
                "GREAT BRITAIN",
                false,
                "HPGWGBWZXXX"),
            "countryISO2"),
        Arguments.of(
            new SwiftDataRequest(
                "DIAGON ALLEY, CHARING CROSS ROAD, LONDON, ENGLAND, GB",
                "GRINGOTTS WIZARDING BANK",
                "",
                "GREAT BRITAIN",
                false,
                "HPGWGBWZXXX"),
            "countryISO2"),
        Arguments.of(
            new SwiftDataRequest(
                "DIAGON ALLEY, CHARING CROSS ROAD, LONDON, ENGLAND, GB",
                "GRINGOTTS WIZARDING BANK",
                "GB",
                null,
                false,
                "HPGWGBWZXXX"),
            "countryName"),
        Arguments.of(
            new SwiftDataRequest(
                "DIAGON ALLEY, CHARING CROSS ROAD, LONDON, ENGLAND, GB",
                "GRINGOTTS WIZARDING BANK",
                "GB",
                "",
                false,
                "HPGWGBWZXXX"),
            "countryName"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "AAAABBCCAAA",
        "1234AA12AAA",
        "AB12AA1BAAA",
        "ABCDAABB123",
        "ABCDAABBAAA",
        "ABCDAABBXXX"
      })
  void shouldCreateValidSwiftData(String swiftCode) throws Exception {
    SwiftDataRequest request = new SwiftDataRequest("x", "x", "x", "x", false, swiftCode);

    when(service.create(any())).thenReturn(new ApiResponse("Successfully added new SWIFT data"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/v1/swift-codes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is("Successfully added new SWIFT data")));
  }

  @ParameterizedTest
  @MethodSource("invalidSwiftCodes")
  void shouldReturnErrorWhenInvalidSwiftCode(String swiftCode) throws Exception {
    SwiftDataRequest request = new SwiftDataRequest("x", "x", "x", "x", false, swiftCode);

    when(service.create(any())).thenReturn(new ApiResponse("Successfully added new SWIFT data"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/v1/swift-codes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpectAll(
            jsonPath("$.title", is("Invalid SWIFT data")),
            jsonPath("$.errors.swiftCode").exists(),
            jsonPath("$.errors.swiftCode.message", not(emptyOrNullString())),
            jsonPath("$.errors.swiftCode.rejectedValue", is(swiftCode)));
  }

  @ParameterizedTest
  @MethodSource("invalidRequests")
  void shouldReturnErrorWhenInvalidFieldsOfRequest(SwiftDataRequest request, String field)
      throws Exception {

    when(service.create(any())).thenReturn(new ApiResponse("Successfully added new SWIFT data"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/v1/swift-codes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpectAll(
            jsonPath("$.title", is("Invalid SWIFT data")),
            jsonPath("$.errors." + field).exists(),
            jsonPath("$.errors." + field + ".message", not(emptyOrNullString())));
  }
}
