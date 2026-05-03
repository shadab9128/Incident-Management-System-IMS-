package com.ims.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;

@Document(collection = "work_items")
public class WorkItem {

    @Id
    private String id;

    private String componentId;
    private String severity;
    private String message;

    private int count;

    private LocalDateTime firstSeen;
    private LocalDateTime lastSeen;

    // ✅ STATE MACHINE
    private WorkItemState state = WorkItemState.OPEN;

    // ✅ RCA + METRICS
    private String rca;
    private LocalDateTime resolvedAt;
    private Long mttrSeconds;

    // ✅ CONSTRUCTORS
    public WorkItem() {}

    public WorkItem(String componentId, String severity, String message) {
        this.componentId = componentId;
        this.severity = severity;
        this.message = message;
        this.count = 1;
        this.firstSeen = LocalDateTime.now();
        this.lastSeen = LocalDateTime.now();
        this.state = WorkItemState.OPEN;
    }

    // ===============================
    // 🚀 BUSINESS LOGIC (IMPORTANT)
    // ===============================

    // 👉 Move to INVESTIGATING
    public void startInvestigation() {
        if (this.state != WorkItemState.OPEN) {
            throw new RuntimeException("Can only investigate from OPEN state");
        }
        this.state = WorkItemState.INVESTIGATING;
    }

    // 👉 Resolve incident
    public void resolve() {
        if (this.state != WorkItemState.INVESTIGATING) {
            throw new RuntimeException("Can only resolve from INVESTIGATING state");
        }

        this.state = WorkItemState.RESOLVED;
        this.resolvedAt = LocalDateTime.now();

        // ✅ Calculate MTTR
        if (this.firstSeen != null && this.resolvedAt != null) {
            this.mttrSeconds = Duration.between(firstSeen, resolvedAt).getSeconds();
        }
    }

    // 👉 Close incident (RCA required)
    public void close() {
        if (this.state != WorkItemState.RESOLVED) {
            throw new RuntimeException("Can only close from RESOLVED state");
        }

        if (this.rca == null || this.rca.isEmpty()) {
            throw new RuntimeException("Cannot close without RCA");
        }

        this.state = WorkItemState.CLOSED;
    }

    // ===============================
    // GETTERS & SETTERS
    // ===============================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public LocalDateTime getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(LocalDateTime firstSeen) {
        this.firstSeen = firstSeen;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public WorkItemState getState() {
        return state;
    }

    public void setState(WorkItemState state) {
        this.state = state;
    }

    public String getRca() {
        return rca;
    }

    public void setRca(String rca) {
        this.rca = rca;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Long getMttrSeconds() {
        return mttrSeconds;
    }

    public void setMttrSeconds(Long mttrSeconds) {
        this.mttrSeconds = mttrSeconds;
    }
}