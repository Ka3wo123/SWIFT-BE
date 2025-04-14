package pl.ka3wo.swift.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.ka3wo.swift.exception.NoSwiftDataFound;
import pl.ka3wo.swift.model.SwiftData;
import pl.ka3wo.swift.model.dto.SwiftDataRequest;
import pl.ka3wo.swift.model.mapper.SwiftDataMapper;
import pl.ka3wo.swift.repository.SwiftRepository;
import pl.ka3wo.swift.service.SwiftService;

@ExtendWith(SpringExtension.class)
public class SwiftServiceTest {
  @Mock private SwiftRepository repository;
  @Mock private SwiftDataMapper mapper;

  @InjectMocks private SwiftService service;
  private List<SwiftData> mockData;

  @BeforeEach
  public void setUp() {
    SwiftData data1 =
        new SwiftData(
            "1", "address", "bankName", "countryISO2", "countryName", false, "AAAABBCC", null);
    SwiftData data2 =
        new SwiftData(
            "1", "address", "bankName", "countryISO2", "countryName", false, "BBBBCC11", null);
    mockData = Arrays.asList(data1, data2);
  }

  @Test
  public void shouldThrowNotFound() {
    String swiftCode = "XXXXXXXX";
    when(repository.findBySwiftCode(swiftCode))
        .thenThrow(new NoSwiftDataFound("SWIFT data not found"));

    Exception exception =
        assertThrows(NoSwiftDataFound.class, () -> service.getBySwiftCode(swiftCode));
    assertTrue(exception.getMessage().contains("SWIFT data not found"));
  }

  @Test
  public void shouldCreate() {
    SwiftDataRequest request =
        new SwiftDataRequest(
            "31 AVENUE DE LA COSTA  MONACO, MONACO, 98000",
            "BARCLAYS BANK PLC MONACO",
            "MC",
            "MONACO",
            true,
            "BARCMCMXXXX");
    SwiftData mappedEntity =
        new SwiftData(
            "67fbed254ba4a67c9b44a39a",
            "31 AVENUE DE LA COSTA  MONACO, MONACO, 98000",
            "BARCLAYS BANK PLC MONACO",
            "MC",
            "MONACO",
            true,
            "BARCMCMXXXX",
            null);
    
    when(mapper.fromSwiftDataRequest(request)).thenReturn(mappedEntity);

    service.create(request);
    verify(repository, times(1)).save(mappedEntity);

    ArgumentCaptor<SwiftData> captor = ArgumentCaptor.forClass(SwiftData.class);
    verify(repository).save(captor.capture());
    SwiftData value = captor.getValue();
    assertNotNull(value);
    assertNotNull(value.getId());
    assertEquals("BARCMCMXXXX", value.getSwiftCode());
  }
}
