package com.ims.backend.service;

import com.ims.backend.cache.WorkItemCache;
import com.ims.backend.model.Signal;
import com.ims.backend.model.SignalEvent;
import com.ims.backend.model.WorkItem;
import com.ims.backend.repository.SignalEventRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DebounceService {

    private final MongoTemplate mongoTemplate;
    private final SignalEventRepository signalRepo;
    private final WorkItemCache cache;
    private final AlertService alertService;

    private static final int WINDOW_SECONDS = 10;

    public DebounceService(MongoTemplate mongoTemplate,
                           SignalEventRepository signalRepo,
                           WorkItemCache cache,
                           AlertService alertService) {
        this.mongoTemplate = mongoTemplate;
        this.signalRepo = signalRepo;
        this.cache = cache;
        this.alertService = alertService;
    }

    public void process(Signal signal) {

        LocalDateTime now = LocalDateTime.now();

        // 🔍 Find existing WorkItem in debounce window
        Query query = new Query(
                Criteria.where("componentId").is(signal.getComponentId())
                        .and("severity").is(signal.getSeverity())
                        .and("lastSeen").gte(now.minusSeconds(WINDOW_SECONDS))
        );

        WorkItem existing = mongoTemplate.findOne(query, WorkItem.class);

        WorkItem workItem;

        if (existing == null) {
            // 🆕 CREATE NEW WORKITEM
            WorkItem newItem = new WorkItem(
                    signal.getComponentId(),
                    signal.getSeverity(),
                    signal.getMessage()
            );

            newItem.setFirstSeen(now);
            newItem.setLastSeen(now);
            newItem.setCount(1);

            mongoTemplate.save(newItem);

            workItem = newItem;

            System.out.println("[NEW WORKITEM CREATED]");

        } else {
            // 🔄 UPDATE EXISTING WORKITEM
            Query updateQuery = new Query(Criteria.where("_id").is(existing.getId()));

            Update update = new Update()
                    .inc("count", 1)
                    .set("lastSeen", now);

            mongoTemplate.updateFirst(updateQuery, update, WorkItem.class);

            // keep object consistent for cache
            existing.setCount(existing.getCount() + 1);
            existing.setLastSeen(now);

            workItem = existing;

            System.out.println("[DEBOUNCED → SAME WORKITEM]");
        }

        // 🔥 CACHE UPDATE (WRITE-THROUGH)
        cache.put(workItem);
        System.out.println("CACHE UPDATED: " + workItem.getId());

        // 🔥 ALERT STRATEGY
        alertService.trigger(
                signal.getSeverity(),
                signal.getComponentId(),
                signal.getMessage()
        );

        // 🧾 SAVE RAW SIGNAL (DATA LAKE)
        SignalEvent event = new SignalEvent(
                signal.getComponentId(),
                signal.getSeverity(),
                signal.getMessage(),
                now
        );

        event.setWorkItemId(workItem.getId());

        signalRepo.save(event);

        //System.out.println("[SUCCESS] processed");
    }

    public void saveRawSignal(Signal signal) {

        SignalEvent event = new SignalEvent(
                signal.getComponentId(),
                signal.getSeverity(),
                signal.getMessage(),
                LocalDateTime.now()
        );

        signalRepo.save(event);

        System.out.println("[RAW SIGNAL SAVED]");
    }
}