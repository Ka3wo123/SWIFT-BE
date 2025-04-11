package pl.ka3wo.swift.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ka3wo.swift.model.dto.ApiResponse;
import pl.ka3wo.swift.model.dto.SwiftDataCountryDto;
import pl.ka3wo.swift.model.dto.SwiftDataDto;
import pl.ka3wo.swift.model.dto.SwiftDataRequest;
import pl.ka3wo.swift.service.SwiftService;

@RestController
@RequestMapping("/v1/swift-codes")
public class SwiftController {
  private final SwiftService swiftService;

  public SwiftController(SwiftService swiftService) {
    this.swiftService = swiftService;
  }

  @GetMapping("/")
  public ResponseEntity<List<SwiftDataDto>> getAll() {
    return ResponseEntity.ok(swiftService.getAll());
  }

  @GetMapping("/{swift-code}")
  public ResponseEntity<SwiftDataDto> getBySwiftCode(@PathVariable("swift-code") String swiftCode) {
    return ResponseEntity.ok(swiftService.getBySwiftCode(swiftCode));
  }

  @GetMapping("/country/{countryISO2code}")
  public ResponseEntity<SwiftDataCountryDto> getByCountryISO2code(
      @PathVariable("countryISO2code") String countryISO2code) {
    return ResponseEntity.ok(swiftService.getByCountryISO2code(countryISO2code));
  }

  @PostMapping("/")
  public ResponseEntity<ApiResponse> save(@Valid @RequestBody SwiftDataRequest swiftData) {
    return ResponseEntity.ok(swiftService.save(swiftData));
  }

  @DeleteMapping("/{swift-code}")
  public ResponseEntity<ApiResponse> deleteBySwiftCode(
      @PathVariable("swift-code") String swiftCode) {
    return ResponseEntity.ok(swiftService.deleteOneBySwiftCode(swiftCode));
  }
}
