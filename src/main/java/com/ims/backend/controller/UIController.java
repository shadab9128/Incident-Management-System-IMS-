package com.ims.backend.controller;

import com.ims.backend.cache.WorkItemCache;
import com.ims.backend.model.WorkItem;
import com.ims.backend.repository.WorkItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/ui")
public class UIController {

    private final WorkItemRepository repo;
    private final WorkItemCache cache;   // ✅ CACHE

    public UIController(WorkItemRepository repo, WorkItemCache cache) {
        this.repo = repo;
        this.cache = cache;
    }

    @GetMapping("/incidents")
    public String getIncidents() {

        // 🔥 USE CACHE FIRST
        List<WorkItem> list = cache.getAll();

        if (list.isEmpty()) {
            System.out.println("CACHE EMPTY → FALLBACK TO DB");
            list = repo.findAll();
        } else {
            System.out.println("Fetching from CACHE");
        }

        list.sort(Comparator.comparing(WorkItem::getSeverity));

        StringBuilder html = new StringBuilder();

        for (WorkItem w : list) {
            html.append("""
                <div class='card'
                     hx-get="/ui/incidents/%s"
                     hx-target="#details"
                     hx-swap="innerHTML">

                    <div><b>%s</b></div>
                    <div class='severity %s'>%s</div>
                    <div>Status: %s</div>

                </div>
            """.formatted(
                    w.getId(),
                    w.getComponentId(),
                    w.getSeverity(),
                    w.getSeverity(),
                    w.getState()
            ));
        }

        return html.toString();
    }

    @GetMapping("/incidents/{id}")
    public String getIncidentDetail(@PathVariable String id) {

        // 🔥 TRY CACHE FIRST
        WorkItem w = cache.get(id);

        if (w == null) {
            System.out.println("DETAIL → CACHE MISS → DB HIT");
            w = repo.findById(id).orElseThrow();
        } else {
            System.out.println("DETAIL → FROM CACHE");
        }

        return """
            <div class='section'>
                <h3>Incident Detail</h3>
                <p><b>Component:</b> %s</p>
                <p><b>Severity:</b> %s</p>
                <p><b>Status:</b> %s</p>
                <p><b>Count:</b> %d</p>
            </div>

            <div class='section'>
                <h3>RCA Form</h3>

                <form hx-post="/ui/incidents/%s/rca"
                      hx-target="#details"
                      hx-swap="innerHTML">

                    <label>Incident Start</label>
                    <input type="datetime-local" name="start"/>

                    <label>Incident End</label>
                    <input type="datetime-local" name="end"/>

                    <label>Root Cause</label>
                    <select name="category">
                        <option>Infra</option>
                        <option>Application</option>
                        <option>Network</option>
                    </select>

                    <label>Fix Applied</label>
                    <textarea name="fix"></textarea>

                    <label>Prevention</label>
                    <textarea name="prevention"></textarea>

                    <button type="submit">Submit RCA</button>

                </form>
            </div>
        """.formatted(
                w.getComponentId(),
                w.getSeverity(),
                w.getState(),
                w.getCount(),
                id
        );
    }

    @PostMapping("/incidents/{id}/rca")
    public String submitRCA(@PathVariable String id,
                            @RequestParam String start,
                            @RequestParam String end,
                            @RequestParam String category,
                            @RequestParam String fix,
                            @RequestParam String prevention) {

        WorkItem w = repo.findById(id).orElseThrow();

        w.setRca(category + " | " + fix + " | " + prevention);

        repo.save(w);

        // 🔥 UPDATE CACHE AFTER WRITE
        cache.put(w);

        return "<h3>✅ RCA Submitted Successfully</h3>";
    }
}