package com.andersenlab.cdc.debezium.connectors;

import com.andersenlab.cdc.debezium.services.MeetupFacade;
import io.debezium.config.Configuration;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MongoConnector {

  private final Executor executor;
  private final DebeziumEngine<ChangeEvent<String, String>> debeziumEngine;
  private final MeetupFacade meetupFacade;

  public MongoConnector(Configuration mongodbConnector, MeetupFacade meetupFacade) {
    this.executor = Executors.newSingleThreadExecutor();
    this.debeziumEngine = DebeziumEngine.create(Json.class)
        .using(mongodbConnector.asProperties())
        .notifying(this::handleChangeEvent)
        .build();
    this.meetupFacade = meetupFacade;
  }

  @SneakyThrows
  private void handleChangeEvent(ChangeEvent<String, String> sourceRecordRecordChangeEvent) {
    String key = sourceRecordRecordChangeEvent.key();
    String value = sourceRecordRecordChangeEvent.value();
    log.debug("Key={}, Value={}", key, value);

    if (Objects.isNull(value)) {
      log.debug("Skipping empty value");
      return;
    }

    meetupFacade.synchronize(key, value);
  }

  @PostConstruct
  private void start() {
    this.executor.execute(debeziumEngine);
  }

  @PreDestroy
  private void stop() throws IOException {
    if (this.debeziumEngine != null) {
      this.debeziumEngine.close();
    }
  }
}
