package com.ims.backend.service;

import com.ims.backend.model.Signal;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SignalService {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Async("taskExecutor")
    public void processSignal(Signal signal) {
        counter.incrementAndGet();

        System.out.println("[PROCESSING] " 
            + signal.getComponentId() + " | " 
            + signal.getSeverity());
    }

    public int getAndResetCounter() {
        return counter.getAndSet(0);
    }
}