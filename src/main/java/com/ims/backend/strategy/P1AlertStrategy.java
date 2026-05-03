package com.ims.backend.strategy;

import org.springframework.stereotype.Component;

/**
 * P1 → Critical Alerts (e.g., PagerDuty)
 */
@Component
public class P1AlertStrategy implements AlertStrategy {

    @Override
    public void sendAlert(String componentId, String message) {
        System.out.println("🚨 P1 ALERT → PagerDuty | " 
                + componentId + " | " + message);
    }
}