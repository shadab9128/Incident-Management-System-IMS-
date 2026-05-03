package com.ims.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "signals_raw")
public class SignalEvent {

    @Id
    private String id;

    private String componentId;
    private String severity;
    private String message;
    private LocalDateTime timestamp;

    private String workItemId; // 🔗 link to aggregated incident

    public SignalEvent() {}

    public SignalEvent(String componentId, String severity, String message, LocalDateTime timestamp) {
        this.componentId = componentId;
        this.severity = severity;
        this.message = message;
        this.timestamp = timestamp;
    }

    // getters & setters
    public String getId() { return id; }
    public String getComponentId() { return componentId; }
    public String getSeverity() { return severity; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getWorkItemId() { return workItemId; }

    public void setWorkItemId(String workItemId) { this.workItemId = workItemId; }
}