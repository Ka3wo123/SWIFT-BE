package pl.ka3wo.swift.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.ka3wo.swift.exception.DuplicateSwiftCodeException;
import pl.ka3wo.swift.exception.NoSwiftDataFound;
import pl.ka3wo.swift.model.SwiftData;
import pl.ka3wo.swift.model.dto.SwiftDataRequest;
import pl.ka3wo.swift.model.mapper.SwiftDataMapper;
import pl.ka3wo.swift.repository.SwiftRepository;

@ExtendWith(MockitoExtension.class)
public class SwiftServiceTest {
  @Mock private SwiftRepository repository;
  @Mock private SwiftDataMapper mapper;

  @InjectMocks private SwiftService service;
  private static final String SWIFT_CODE = "AAAABBCC";
  
  @Test
  public void shouldThrowNotFound() {
    String swiftCode = "XXXXXXXX";
    when(repository.findBySwiftCode(swiftCode))
        .thenThrow(new NoSwiftDataFound("SWIFT data not found"));

    Exception exception =
        assertThrows(NoSwiftDataFound.class, () -> service.getBySwiftCode(swiftCode));
    assertEquals("SWIFT data not found", exception.getMessage());
  }

  @Test
  public void shouldCreateNewSwiftData() {
    SwiftDataRequest request =
            new SwiftDataRequest(
                    "31 avenue de la costa  monaco, monaco, 98000",
                    "barclays bank plc monaco",
                    "mc",
                    "monaco",
                    true,
                    "BARCMCMXXX");

    SwiftData mappedEntity =
            new SwiftData(
                    "67fbed254ba4a67c9b44a39a",
                    "31 avenue de la costa  monaco, monaco, 98000",
                    "barclays bank plc monaco",
                    "mc",
                    "monaco",
                    true,
                    "BARCMCMXXX",
                    null);

    when(mapper.fromSwiftDataRequest(request)).thenReturn(mappedEntity);

    service.create(request);
    verify(repository, times(1)).save(mappedEntity);

    ArgumentCaptor<SwiftData> captor = ArgumentCaptor.forClass(SwiftData.class);
    verify(repository).save(captor.capture());
    SwiftData value = captor.getValue();
    assertNotNull(value);
    assertNotNull(value.getId());
    assertEquals(value.getAddress(), value.getAddress().toUpperCase());
    assertEquals(value.getBankName(), value.getBankName().toUpperCase());
    assertEquals(value.getCountryISO2(), value.getCountryISO2().toUpperCase());
    assertEquals(value.getCountryName(), value.getCountryName().toUpperCase());
    assertEquals("BARCMCMXXX", value.getSwiftCode());
  }

  @Test
  public void shouldThrowConflictException() {
    SwiftDataRequest request =
        new SwiftDataRequest(
            "31 AVENUE DE LA COSTA  MONACO, MONACO, 98000",
            "BARCLAYS BANK PLC MONACO",
            "MC",
            "MONACO",
            true,
            SWIFT_CODE);

    when(mapper.fromSwiftDataRequest(request))
        .thenThrow(
            new DuplicateSwiftCodeException(
                String.format("Data with SWIFT code %s already exists", SWIFT_CODE)));

    Exception exception =
        assertThrows(DuplicateSwiftCodeException.class, () -> service.create(request));
    assertEquals(
        String.format("Data with SWIFT code %s already exists", SWIFT_CODE),
        exception.getMessage());
  }

  @Test
  public void shouldDeleteSwiftDataSuccessfully() {
    when(repository.existsBySwiftCode(SWIFT_CODE)).thenReturn(true);
    var response = service.deleteOneBySwiftCode(SWIFT_CODE);

    verify(repository, times(1)).deleteBySwiftCode(SWIFT_CODE);
    assertNotNull(response);
    assertEquals("Successfully deleted SWIFT data", response.getMessage());
  }

  @Test
  public void shouldThrowNotFoundWhenDeletingNonExistingSwiftData() {
    String swiftCode = "NONEXIST";

    Exception exception =
        assertThrows(NoSwiftDataFound.class, () -> service.deleteOneBySwiftCode(swiftCode));
    assertEquals(
        String.format("SWIFT data with SWIFT code %s not found", swiftCode),
        exception.getMessage());
  }
}
