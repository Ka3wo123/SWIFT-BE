package pl.ka3wo.swift.repoistory;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import pl.ka3wo.swift.model.SwiftData;

public interface SwiftRepository extends MongoRepository<SwiftData, String> {
  void deleteBySwiftCode(String swiftCode);

  Optional<SwiftData> findBySwiftCode(String swiftCode);

  List<SwiftData> findByCountryISO2(String countryISO2code);

  boolean existsBySwiftCode(String swiftCode);
}
