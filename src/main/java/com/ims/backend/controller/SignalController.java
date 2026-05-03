package com.ims.backend.controller;

import com.ims.backend.dto.SignalRequest;
import com.ims.backend.mapper.SignalMapper;
import com.ims.backend.service.SignalProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signals")
public class SignalController {

    private final SignalProducer producer;

    public SignalController(SignalProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public String ingestSignal(@RequestBody SignalRequest request) {
        producer.send(SignalMapper.toModel(request));
        return "queued";
    }
}