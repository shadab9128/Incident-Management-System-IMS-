package com.ims.backend.repository;

import com.ims.backend.model.SignalEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SignalEventRepository extends MongoRepository<SignalEvent, String> {
}