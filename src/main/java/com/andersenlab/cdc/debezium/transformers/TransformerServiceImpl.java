package com.andersenlab.cdc.debezium.transformers;

import com.andersenlab.cdc.debezium.models.Meetup;
import com.andersenlab.cdc.debezium.models.cdc.Action;
import com.andersenlab.cdc.debezium.models.cdc.CdcRecord;
import com.andersenlab.cdc.debezium.models.cdc.Payload;
import com.andersenlab.cdc.debezium.models.cdc.TransformerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransformerServiceImpl implements TransformerService {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  @SneakyThrows
  public TransformerResponse getModel(String key, String value) {
    CdcRecord valueRecord = objectMapper.readValue(value, CdcRecord.class);

    Action action = Action.findByValue(valueRecord.getPayload().getOp());
    Meetup model = getModel(key, valueRecord.getPayload(), action);
    return TransformerResponse.builder()
        .action(action)
        .model(model)
        .build();
  }

  private String getKeyId(String keyRecord) throws JsonProcessingException {
    JsonNode rootNode = objectMapper.readTree(keyRecord);
    String idJson = rootNode.get("payload").get("id").asText();
    JsonNode idNode = objectMapper.readTree(idJson);
    return idNode.get("$oid").asText();
  }

  @SneakyThrows
  private Meetup getModel(String key, Payload payload, Action action) {
    String keyId = getKeyId(key);
    String rawJson = getRawJson(payload, action);
    if (Objects.isNull(rawJson)) {
      return Meetup.builder().id(keyId).build();
    }
    try {
      Meetup meetup = objectMapper.readValue(rawJson, Meetup.class);
      meetup.setId(keyId);
      return meetup;
    } catch (IOException e) {
      log.debug("Error when we try to get model from {}", rawJson);
      throw e;
    }
  }

  private String getRawJson(Payload payload, Action action) {
    return Action.DELETE.equals(action) ? payload.getBefore() : payload.getAfter();
  }
}
