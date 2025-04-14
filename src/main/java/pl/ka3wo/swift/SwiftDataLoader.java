package pl.ka3wo.swift;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.ka3wo.swift.service.CSVSwiftLoaderService;

@Component
public class SwiftDataLoader implements CommandLineRunner {
  private final CSVSwiftLoaderService csvSwiftLoaderService;

  public SwiftDataLoader(CSVSwiftLoaderService csvSwiftLoaderService) {
    this.csvSwiftLoaderService = csvSwiftLoaderService;
  }

  @Override
  public void run(String... args) {
    csvSwiftLoaderService.loadSwiftDataFromCsv();
  }
}
