package com.ims.backend.strategy;

import org.springframework.stereotype.Component;

/**
 * P2 → Warning Alerts (e.g., Email)
 */
@Component
public class P2AlertStrategy implements AlertStrategy {

    @Override
    public void sendAlert(String componentId, String message) {
        System.out.println("⚠️ P2 ALERT → Email | " 
                + componentId + " | " + message);
    }
}