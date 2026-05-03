package com.ims.backend.service;

import com.ims.backend.config.RabbitMQConfig;
import com.ims.backend.model.Signal;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class SignalProducer {

    private final RabbitTemplate rabbitTemplate;

    public SignalProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(Signal signal) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                signal
        );
    }
}