package pl.ka3wo.swift.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.ka3wo.swift.exception.NoSwiftDataFound;
import pl.ka3wo.swift.model.dto.*;
import pl.ka3wo.swift.service.SwiftService;

@WebMvcTest(SwiftController.class)
@AutoConfigureJsonTesters
public class SwiftControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockitoBean private SwiftService swiftService;

  @Autowired private JacksonTester<SwiftDataRequest> jsonSwiftDataRequest;
  private SwiftDataDto swiftDataDto;
  private SwiftDataCountryDto swiftDataCountryDto;
  private final String SWIFT_CODE = "HPGWGBWZ000";
  private final String COUNTRY_ISO_2_CODE = "GB";
  private final String COUNTRY_NAME = "GREAT BRITAIN";
  private final String BASE_URL = "/v1/swift-codes/";

  @BeforeEach
  public void setUp() {
    swiftDataDto =
        new SwiftDataDto(
            "DIAGON ALLEY CHARING CROSS ROAD LONDON ENGLAND GB",
            "GRINGOTTS WIZARDING BANK",
            COUNTRY_ISO_2_CODE,
            COUNTRY_NAME,
            false,
            SWIFT_CODE,
            null);
    swiftDataCountryDto =
        new SwiftDataCountryDto(COUNTRY_ISO_2_CODE, COUNTRY_NAME, List.of(swiftDataDto));
  }

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
                "DIAGON ALLEY CHARING CROSS ROAD LONDON ENGLAND GB",
                null,
                "GB",
                "GREAT BRITAIN",
                false,
                "HPGWGBWZXXX"),
            "bankName"),
        Arguments.of(
            new SwiftDataRequest(
                "DIAGON ALLEY CHARING CROSS ROAD LONDON ENGLAND GB",
                "",
                "GB",
                "GREAT BRITAIN",
                false,
                "HPGWGBWZXXX"),
            "bankName"),
        Arguments.of(
            new SwiftDataRequest(
                "DIAGON ALLEY CHARING CROSS ROAD LONDON ENGLAND GB",
                "GRINGOTTS WIZARDING BANK",
                null,
                "GREAT BRITAIN",
                false,
                "HPGWGBWZXXX"),
            "countryISO2"),
        Arguments.of(
            new SwiftDataRequest(
                "DIAGON ALLEY CHARING CROSS ROAD LONDON ENGLAND GB",
                "GRINGOTTS WIZARDING BANK",
                "",
                "GREAT BRITAIN",
                false,
                "HPGWGBWZXXX"),
            "countryISO2"),
        Arguments.of(
            new SwiftDataRequest(
                "DIAGON ALLEY CHARING CROSS ROAD LONDON ENGLAND GB",
                "GRINGOTTS WIZARDING BANK",
                "GB",
                null,
                false,
                "HPGWGBWZXXX"),
            "countryName"),
        Arguments.of(
            new SwiftDataRequest(
                "DIAGON ALLEY CHARING CROSS ROAD LONDON ENGLAND GB",
                "GRINGOTTS WIZARDING BANK",
                "GB",
                "",
                false,
                "HPGWGBWZXXX"),
            "countryName"));
  }

  @Test
  public void shouldGetOneBySwiftCode() throws Exception {
    when(swiftService.getBySwiftCode(SWIFT_CODE)).thenReturn(swiftDataDto);

    mockMvc
        .perform(get(BASE_URL + "{swiftCode}", SWIFT_CODE))
        .andExpect(status().isOk())
        .andExpectAll(
            jsonPath("address", equalTo(swiftDataDto.address())),
            jsonPath("bankName", equalTo(swiftDataDto.bankName())),
            jsonPath("countryISO2", equalTo(swiftDataDto.countryISO2())),
            jsonPath("countryName", equalTo(swiftDataDto.countryName())),
            jsonPath("isHeadquarter", equalTo(swiftDataDto.isHeadquarter())),
            jsonPath("swiftCode", equalTo(swiftDataDto.swiftCode())),
            jsonPath("branches").doesNotExist());
  }

  @Test
  public void shouldGetOneByCountryISO2Code() throws Exception {
    when(swiftService.getByCountryISO2code(COUNTRY_ISO_2_CODE)).thenReturn(swiftDataCountryDto);

    mockMvc
        .perform(get(BASE_URL + "country/{countryISO2Code}", COUNTRY_ISO_2_CODE))
        .andExpect(status().isOk())
        .andExpectAll(
            jsonPath("countryISO2", equalTo(COUNTRY_ISO_2_CODE)),
            jsonPath("countryName", equalTo(COUNTRY_NAME)),
            jsonPath("swiftCodes", is(not(emptyArray()))));
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

    when(swiftService.create(request)).thenReturn(new CreateSwiftDataResponse());

    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSwiftDataRequest.write(request).getJson()))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is("Successfully added new SWIFT data")));
  }

  @ParameterizedTest
  @MethodSource("invalidSwiftCodes")
  void shouldReturnErrorWhenInvalidSwiftCode(String swiftCode) throws Exception {
    SwiftDataRequest request = new SwiftDataRequest("x", "x", "x", "x", false, swiftCode);

    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSwiftDataRequest.write(request).getJson()))
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

    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSwiftDataRequest.write(request).getJson()))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpectAll(
            jsonPath("$.title", is("Invalid SWIFT data")),
            jsonPath("$.errors." + field).exists(),
            jsonPath("$.errors." + field + ".message", not(emptyOrNullString())));
  }

  @Test
  public void shouldReturnErrorWhenDeletingNonExistentSwiftData() throws Exception {
    String swiftCode = "NONEXIST123";

    when(swiftService.deleteOneBySwiftCode(swiftCode))
        .thenThrow(
            new NoSwiftDataFound(
                String.format("SWIFT data with SWIFT code %s not found", swiftCode)));

    mockMvc
        .perform(
            delete(BASE_URL + "{swiftCode}", swiftCode))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.title", is("SWIFT data not found")))
        .andExpect(
            jsonPath("$.detail", is("SWIFT data with SWIFT code " + swiftCode + " not found")));
  }

  @Test
  public void shouldSuccessfullyDeleteSwiftData() throws Exception {
    when(swiftService.deleteOneBySwiftCode(SWIFT_CODE)).thenReturn(new DeleteSwiftDataResponse());

    mockMvc
        .perform(delete(BASE_URL + "{swiftCode}", SWIFT_CODE))
        .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("message", equalTo("Successfully deleted SWIFT data")));
    ;
  }
}
