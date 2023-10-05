package com.andersenlab.cdc.debezium.services;

import com.andersenlab.cdc.debezium.models.Meetup;
import com.andersenlab.cdc.debezium.models.cdc.TransformerResponse;
import com.andersenlab.cdc.debezium.repositories.CdcRepository;
import com.andersenlab.cdc.debezium.transformers.TransformerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class MeetupFacadeImpl implements MeetupFacade {

  private final CdcRepository cdcRepository;
  private final TransformerService transformerService;

  @Override
  public void synchronize(String key, String value) {
    TransformerResponse model = transformerService.getModel(key, value);
    synchronize(model);
  }

  private void synchronize(TransformerResponse transformerResponse) {
    if (transformerResponse == null) {
      return;
    }

    Meetup meetup = transformerResponse.getModel();
    switch (transformerResponse.getAction()) {
      case READ, CREATE, UPDATE -> cdcRepository.save(meetup);
      case DELETE -> cdcRepository.deleteById(meetup.getId());
      default -> log.debug("Unknown operation");
    }
  }
}
