package pl.ka3wo.swift.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.ka3wo.swift.model.SwiftData;
import pl.ka3wo.swift.repository.SwiftRepository;

@ExtendWith(MockitoExtension.class)
public class CSVSwiftLoaderServiceTest {

  @Mock private SwiftRepository swiftRepository;

  @InjectMocks private CSVSwiftLoaderService csvSwiftLoaderService;

  @Test
  public void shouldExtractAndLoadDataAsEntities() {
    when(swiftRepository.findBySwiftCode(anyString())).thenReturn(Optional.empty());

    csvSwiftLoaderService.loadSwiftDataFromCsv();

    ArgumentCaptor<SwiftData> captor = ArgumentCaptor.forClass(SwiftData.class);
    verify(swiftRepository, atLeastOnce()).save(captor.capture());

    List<SwiftData> savedEntities = captor.getAllValues();
    assertNotNull(savedEntities);
    assertFalse(savedEntities.isEmpty());

    SwiftData hq =
        savedEntities.stream().filter(SwiftData::getIsHeadquarter).findFirst().orElse(null);

    assertNotNull(hq);
    assertTrue(hq.getSwiftCode().endsWith("XXX"));
  }
}
