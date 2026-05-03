package com.ims.backend.controller;

import com.ims.backend.model.WorkItem;
import com.ims.backend.model.WorkItemState;
import com.ims.backend.service.WorkItemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workitems")
public class WorkItemController {

    private final WorkItemService service;

    public WorkItemController(WorkItemService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public WorkItem get(@PathVariable String id) {
        return service.getById(id);
    }

    // Transition endpoint
    @PostMapping("/{id}/transition")
    public WorkItem transition(
            @PathVariable String id,
            @RequestParam WorkItemState state,
            @RequestParam(required = false) String rca
    ) {
        return service.transition(id, state, rca);
    }
}