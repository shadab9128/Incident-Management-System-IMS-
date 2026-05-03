package com.ims.backend.service;

import com.ims.backend.model.WorkItem;
import com.ims.backend.model.WorkItemState;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class WorkItemService {

    private final MongoTemplate mongoTemplate;

    public WorkItemService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public WorkItem getById(String id) {
        return mongoTemplate.findById(id, WorkItem.class);
    }

    public WorkItem transition(String id, WorkItemState target, String rca) {

        WorkItem wi = getById(id);
        if (wi == null) {
            throw new RuntimeException("WorkItem not found");
        }

        WorkItemState current = wi.getState();

        // 🔒 Enforce valid transitions
        switch (current) {
            case OPEN -> {
                if (target != WorkItemState.INVESTIGATING) {
                    throw new RuntimeException("OPEN → only INVESTIGATING allowed");
                }
            }
            case INVESTIGATING -> {
                if (target != WorkItemState.RESOLVED) {
                    throw new RuntimeException("INVESTIGATING → only RESOLVED allowed");
                }
            }
            case RESOLVED -> {
                if (target != WorkItemState.CLOSED) {
                throw new RuntimeException("RESOLVED → only CLOSED allowed");
                }

            // ✅ Check incoming RCA (NOT existing DB value)
            if (rca == null || rca.isBlank()) {
                throw new RuntimeException("Cannot CLOSE without RCA");
            }
            }
            case CLOSED -> throw new RuntimeException("Already CLOSED");
        }

        // 🧠 Apply changes
        Query q = new Query(Criteria.where("_id").is(id));
        Update u = new Update().set("state", target);

        if (target == WorkItemState.RESOLVED) {
            LocalDateTime now = LocalDateTime.now();
            u.set("resolvedAt", now);

            // MTTR = resolvedAt - firstSeen
            long mttr = Duration.between(wi.getFirstSeen(), now).getSeconds();
            u.set("mttrSeconds", mttr);
        }

        if (rca != null && !rca.isBlank()) {
            u.set("rca", rca);
        }

        mongoTemplate.updateFirst(q, u, WorkItem.class);

        return mongoTemplate.findById(id, WorkItem.class);
    }
}