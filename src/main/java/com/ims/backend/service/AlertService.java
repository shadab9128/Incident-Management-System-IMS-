package com.ims.backend.service;

import com.ims.backend.strategy.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AlertService {

    private final Map<String, AlertStrategy> strategyMap;

    public AlertService(P1AlertStrategy p1,
                        P2AlertStrategy p2,
                        P3AlertStrategy p3) {

        strategyMap = Map.of(
                "P1", p1,
                "P2", p2,
                "P3", p3
        );
    }

    public void trigger(String severity, String componentId, String message) {

        AlertStrategy strategy = strategyMap.get(severity);

        if (strategy != null) {
            strategy.sendAlert(componentId, message);
        } else {
            System.out.println("No strategy found for " + severity);
        }
    }
}