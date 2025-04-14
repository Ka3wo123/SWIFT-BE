package pl.ka3wo.swift.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<ApiResponse> create(@Valid @RequestBody SwiftDataRequest swiftData) {
    return new ResponseEntity<>(swiftService.create(swiftData), HttpStatus.CREATED);
  }

  @DeleteMapping("/{swift-code}")
  public ResponseEntity<ApiResponse> deleteBySwiftCode(
      @PathVariable("swift-code") String swiftCode) {
    return ResponseEntity.ok(swiftService.deleteOneBySwiftCode(swiftCode));
  }
}
