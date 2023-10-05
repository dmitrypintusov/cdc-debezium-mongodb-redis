package com.andersenlab.cdc.debezium.configs;

import io.debezium.config.Configuration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class DebeziumConfig {

  @Bean
  public Configuration mongodbConnector() {
    String filePath = System.getenv("OFFSET_PATH");
    createOffsetFile(filePath);

    return Configuration.create()
        // engine properties
        .with("name", "meetups-cdc-connector")
        .with("connector.class", "io.debezium.connector.mongodb.MongoDbConnector")
        .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
        .with("offset.storage.file.filename", filePath)
        .with("offset.flush.interval.ms", 60000)
        .with("errors.max.retries", 5)
        .with("value.converter", "org.apache.kafka.connect.json.JsonConverter")
        .with("value.converter.schemas.enable", "true")

        // connector specific properties
        .with("mongodb.connection.string",
            "mongodb://localhost:27017,localhost:27018,localhost:27019/cdc?readPreference=primary&replicaSet=my-mongo-set")
        .with("topic.prefix", "mongodb-connector")
        .with("snapshot.delay.ms", 100)
        .with("errors.log.include.messages", "true")
        .with("mongodb.uuid-representation", "standard")
        .build();
  }

  private void createOffsetFile(String offsetFilePath) {
    Path path = Paths.get(offsetFilePath);
    if (Files.exists(path)) {
      return;
    }

    createFile(path);
  }

  @SneakyThrows
  private void createFile(Path path) {
    Files.createDirectories(path.getParent());
    Files.createFile(path);
  }
}
