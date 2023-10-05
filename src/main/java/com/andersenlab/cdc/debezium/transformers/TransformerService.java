package com.andersenlab.cdc.debezium.transformers;

import com.andersenlab.cdc.debezium.models.cdc.TransformerResponse;

public interface TransformerService {

  TransformerResponse getModel(String key, String value);
}
