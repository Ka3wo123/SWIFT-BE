package pl.ka3wo.swift.repoistory;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.ka3wo.swift.model.SwiftData;

import java.util.UUID;

public interface SwiftRepository extends MongoRepository<SwiftData, UUID> {}
