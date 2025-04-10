package pl.ka3wo.swift.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import pl.ka3wo.swift.model.SwiftData;

@Configuration
public class MongoIndexConfig {
  private final MongoTemplate mongoTemplate;

  public MongoIndexConfig(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @PostConstruct
  public void addIndexes() {
    mongoTemplate
        .indexOps(SwiftData.class)
        .ensureIndex(new Index().on("swiftCode", Sort.Direction.ASC).unique());

    mongoTemplate.indexOps(SwiftData.class)
            .ensureIndex(new Index().on("countryISO2", Sort.Direction.ASC));
  }


}
