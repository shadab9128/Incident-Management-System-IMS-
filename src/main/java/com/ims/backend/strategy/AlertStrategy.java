package com.ims.backend.strategy;

/**
 * Strategy Interface for Alerting based on severity.
 */
public interface AlertStrategy {

    /**
     * Trigger alert
     *
     * @param componentId affected component
     * @param message alert message
     */
    void sendAlert(String componentId, String message);
}