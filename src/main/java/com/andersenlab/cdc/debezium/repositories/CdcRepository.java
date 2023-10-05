package com.andersenlab.cdc.debezium.repositories;

import com.andersenlab.cdc.debezium.models.Meetup;
import org.springframework.data.repository.CrudRepository;

public interface CdcRepository extends CrudRepository<Meetup, String> {

}
