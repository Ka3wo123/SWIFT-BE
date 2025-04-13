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
public class UnitTest {
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
            new SwiftDataRequest(null, "bank", "PL", "Poland", false, "ABCDEF12"), "address"),
        Arguments.of(
            new SwiftDataRequest("addr", null, "PL", "Poland", false, "ABCDEF12"), "bankName"),
        Arguments.of(
            new SwiftDataRequest("addr", "", "PL", "Poland", false, "ABCDEF12"), "bankName"),
        Arguments.of(
            new SwiftDataRequest("addr", "bank", null, "Poland", false, "ABCDEF12"), "countryISO2"),
        Arguments.of(
            new SwiftDataRequest("addr", "bank", "", "Poland", false, "ABCDEF12"), "countryISO2"),
        Arguments.of(
            new SwiftDataRequest("addr", "bank", "PL", null, false, "ABCDEF12"), "countryName"),
        Arguments.of(
            new SwiftDataRequest("addr", "bank", "PL", "", false, "ABCDEF12"), "countryName"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"AAAABBCC", "1234AA12", "AB12AA1B", "ABCDAABB123", "ABCDAABBAAA", "ABCDAABBXXX"})
  void shouldCreateValidSwiftData(String swiftCode) throws Exception {
    SwiftDataRequest request = new SwiftDataRequest("x", "x", "x", "x", false, swiftCode);

    when(service.create(any())).thenReturn(new ApiResponse("Successfully added new SWIFT data"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/v1/swift-codes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
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
        .andExpect(jsonPath("$.title", is("Invalid SWIFT data")))
        .andExpect(jsonPath("$.errors.swiftCode").exists())
        .andExpect(jsonPath("$.errors.swiftCode.message", not(emptyOrNullString())))
        .andExpect(jsonPath("$.errors.swiftCode.rejectedValue", is(swiftCode)));
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
        .andExpect(jsonPath("$.title", is("Invalid SWIFT data")))
        .andExpect(jsonPath("$.errors." + field).exists())
        .andExpect(jsonPath("$.errors." + field + ".message", not(emptyOrNullString())));
  }
}
