package com.ims.backend.strategy;

import org.springframework.stereotype.Component;

/**
 * P3 → Informational Alerts (logging only)
 */
@Component
public class P3AlertStrategy implements AlertStrategy {

    @Override
    public void sendAlert(String componentId, String message) {
        System.out.println("ℹ️ P3 ALERT → Log | " 
                + componentId + " | " + message);
    }
}