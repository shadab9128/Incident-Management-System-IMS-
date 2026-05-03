package com.ims.backend.repository;

import com.ims.backend.model.WorkItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WorkItemRepository extends MongoRepository<WorkItem, String> {

    Optional<WorkItem> findByComponentIdAndSeverity(String componentId, String severity);
}