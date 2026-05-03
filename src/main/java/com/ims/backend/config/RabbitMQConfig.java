package com.ims.backend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 🔹 Names
    public static final String QUEUE = "signal.queue";
    public static final String EXCHANGE = "signal.exchange";
    public static final String ROUTING_KEY = "signal.routing";

    // ----------------------------
    // 🧱 Queue + Exchange + Binding
    // ----------------------------

    @Bean
    public Queue queue() {
        // durable = true (survives restarts)
        return new Queue(QUEUE, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    // ----------------------------
    // 🔄 JSON Message Converter
    // ----------------------------

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ----------------------------
    // 📤 Producer (RabbitTemplate)
    // ----------------------------

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter); // ✅ FIX: JSON serialize
        return template;
    }

    // ----------------------------
    // 📥 Consumer (Listener Factory)
    // ----------------------------

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);

        // 🔥 BACKPRESSURE (tune as needed)
        factory.setPrefetchCount(5);        // max messages per consumer
        factory.setConcurrentConsumers(2); // initial consumers
        factory.setMaxConcurrentConsumers(4);

        // ✅ FIX: JSON deserialize → Signal object
        factory.setMessageConverter(messageConverter);

        return factory;
    }
}