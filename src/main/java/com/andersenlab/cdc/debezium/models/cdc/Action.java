package com.andersenlab.cdc.debezium.models.cdc;

import java.util.Arrays;
import java.util.Optional;

public enum Action {
  CREATE("c"),
  UPDATE("u"),
  DELETE("d"),
  READ("r");

  private final String value;

  Action(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static Action findByValue(String value) {
    return Optional.ofNullable(value)
        .map(Action::parseValue)
        .orElse(null);
  }

  private static Action parseValue(String value) {
    return Arrays.stream(values())
        .filter(action -> action.getValue().equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid action value: " + value));
  }
}
