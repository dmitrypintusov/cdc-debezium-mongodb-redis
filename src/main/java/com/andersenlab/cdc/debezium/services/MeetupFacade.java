package com.andersenlab.cdc.debezium.services;

public interface MeetupFacade {

  void synchronize(String key, String value);
}
