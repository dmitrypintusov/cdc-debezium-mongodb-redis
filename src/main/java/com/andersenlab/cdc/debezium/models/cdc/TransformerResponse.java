package com.andersenlab.cdc.debezium.models.cdc;

import com.andersenlab.cdc.debezium.models.Meetup;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransformerResponse {

  Meetup model;
  Action action;
}
