package pl.ka3wo.swift.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.ka3wo.swift.exception.DuplicateSwiftCodeException;
import pl.ka3wo.swift.exception.NoSwiftDataFound;
import pl.ka3wo.swift.model.SwiftData;
import pl.ka3wo.swift.model.dto.ApiResponse;
import pl.ka3wo.swift.model.dto.SwiftDataCountryDto;
import pl.ka3wo.swift.model.dto.SwiftDataDto;
import pl.ka3wo.swift.model.dto.SwiftDataRequest;
import pl.ka3wo.swift.repoistory.SwiftRepository;

@Service
public class SwiftService {
  private final SwiftRepository swiftRepository;
  private final MongoTemplate mongoTemplate;

  public SwiftService(SwiftRepository swiftRepository, MongoTemplate mongoTemplate) {
    this.swiftRepository = swiftRepository;
    this.mongoTemplate = mongoTemplate;
  }

  public SwiftDataDto getBySwiftCode(String swiftCode) {
    Query query = new Query();
    query.addCriteria(Criteria.where("swiftCode").is(swiftCode));

    SwiftData swiftData = mongoTemplate.findOne(query, SwiftData.class);

    if (swiftData == null) {
      throw new NoSwiftDataFound("SWIFT data not found");
    }
    return toDto(swiftData);
  }

  public SwiftDataCountryDto getByCountryISO2code(String countryISO2code) {
    Query query = new Query();
    query.addCriteria(Criteria.where("countryISO2").is(countryISO2code));

    List<SwiftData> swiftData = mongoTemplate.find(query, SwiftData.class);

    if(swiftData.isEmpty()) {
      return new SwiftDataCountryDto(countryISO2code, null, List.of());
    }

    List<SwiftDataDto> swiftDataDtos = swiftData.stream().map(this::toDto).toList();
    String countryName = swiftDataDtos.get(0).countryName();

    return new SwiftDataCountryDto(countryISO2code, countryName, swiftDataDtos);
  }

  public ApiResponse save(SwiftDataRequest swiftData) {
    Query query = new Query();
    query.addCriteria(Criteria.where("swiftCode").is(swiftData.swiftCode()));
    boolean swiftDataExists = mongoTemplate.exists(query, SwiftData.class);

    if (swiftDataExists) {
      throw new DuplicateSwiftCodeException("Data with provided SWIFT code already exists");
    }
    SwiftData entity = toEntity(swiftData);
    swiftRepository.save(entity);
    return new ApiResponse("Added new SWIFT data");
  }

  private SwiftDataDto toDto(SwiftData swiftData) {
    return new SwiftDataDto(
        swiftData.address(),
        swiftData.bankName(),
        swiftData.countryISO2(),
        swiftData.countryName(),
        swiftData.isHeadquarter(),
        swiftData.swiftCode(),
        swiftData.branches());
  }

  private SwiftData toEntity(SwiftDataRequest dataRequest) {
    return new SwiftData(
        null,
        dataRequest.address(),
        dataRequest.bankName(),
        dataRequest.countryISO2(),
        dataRequest.countryName(),
        dataRequest.isHeadquarter(),
        dataRequest.swiftCode(),
        null);
  }
}
