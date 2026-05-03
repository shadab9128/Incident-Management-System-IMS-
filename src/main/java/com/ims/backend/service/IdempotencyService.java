package com.ims.backend.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdempotencyService {

    private final ConcurrentHashMap<String, Boolean> processed = new ConcurrentHashMap<>();

    public boolean isDuplicate(String key) {
        return processed.putIfAbsent(key, true) != null;
    }
}