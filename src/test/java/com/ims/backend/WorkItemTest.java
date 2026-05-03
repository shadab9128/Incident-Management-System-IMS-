package com.ims.backend;

import com.ims.backend.model.WorkItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorkItemTest {

    // ❌ Test: cannot close without RCA
    @Test
    void shouldFailClosingWithoutRCA() {

        WorkItem w = new WorkItem("TEST", "P1", "Error");

        // Move to valid state first
        w.startInvestigation();
        w.resolve();

        Exception ex = assertThrows(RuntimeException.class, w::close);

        assertEquals("Cannot close without RCA", ex.getMessage());
    }

    // ✅ Test: close successfully with RCA
    @Test
    void shouldCloseWhenRCAProvided() {

        WorkItem w = new WorkItem("TEST", "P1", "Error");

        w.startInvestigation();
        w.resolve();
        w.setRca("CPU issue fixed");

        w.close();

        assertEquals("CLOSED", w.getState().name());
    }

    // ✅ Test: MTTR calculation
    @Test
    void shouldCalculateMTTR() {

        WorkItem w = new WorkItem("TEST", "P1", "Error");

        w.startInvestigation();
        w.resolve();

        assertNotNull(w.getMttrSeconds());
        assertTrue(w.getMttrSeconds() >= 0);
    }

    // ❌ Test: invalid state transition
    @Test
    void shouldNotResolveWithoutInvestigation() {

        WorkItem w = new WorkItem("TEST", "P1", "Error");

        Exception ex = assertThrows(RuntimeException.class, w::resolve);

        assertEquals("Can only resolve from INVESTIGATING state", ex.getMessage());
    }
}