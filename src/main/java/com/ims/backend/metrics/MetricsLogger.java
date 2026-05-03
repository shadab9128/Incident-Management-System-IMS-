package com.ims.backend.metrics;

import com.ims.backend.service.SignalService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MetricsLogger {

    private final SignalService signalService;

    public MetricsLogger(SignalService signalService) {
        this.signalService = signalService;
    }

    @Scheduled(fixedRate = 5000)
    public void logThroughput() {
        int count = signalService.getAndResetCounter();
        System.out.println("Signals/sec: " + count);
    }
}