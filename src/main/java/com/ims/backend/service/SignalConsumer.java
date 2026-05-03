package com.ims.backend.service;

import com.ims.backend.config.RabbitMQConfig;
import com.ims.backend.model.Signal;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class SignalConsumer {

    private final DebounceService debounceService;

    public SignalConsumer(DebounceService debounceService) {
        this.debounceService = debounceService;
    }

    // ✅ IMPORTANT: Use custom container factory (with JSON converter + backpressure)
    @RabbitListener(
            queues = RabbitMQConfig.QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consume(Signal signal) {

        try {
            // 🔹 Save raw signal (data lake)
            debounceService.saveRawSignal(signal);

            // 🔹 Process signal (debounce + cache + alert)
            debounceService.process(signal);

            System.out.println("[SUCCESS] processed");

        } catch (Exception e) {
            System.out.println("[ERROR] processing signal: " + e.getMessage());
            throw e; // rethrow so RabbitMQ can retry if configured
        }
    }
}