package pl.ka3wo.swift.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import pl.ka3wo.swift.model.SwiftData;

public interface SwiftRepository extends MongoRepository<SwiftData, String> {
  Optional<SwiftData> findBySwiftCode(String swiftCode);

  List<SwiftData> findByCountryISO2(String countryISO2code);

  List<SwiftData> findBySwiftCodeStartingWith(String prefix);

  boolean existsBySwiftCode(String swiftCode);

  void deleteBySwiftCode(String swiftCode);
}
