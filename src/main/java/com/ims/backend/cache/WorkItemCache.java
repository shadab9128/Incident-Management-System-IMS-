package com.ims.backend.cache;

import com.ims.backend.model.WorkItem;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WorkItemCache {

    private final Map<String, WorkItem> cache = new ConcurrentHashMap<>();

    public void put(WorkItem item) {
        cache.put(item.getId(), item);
    }

    public List<WorkItem> getAll() {
        return new ArrayList<>(cache.values());
    }

    public WorkItem get(String id) {
        return cache.get(id);
    }
}