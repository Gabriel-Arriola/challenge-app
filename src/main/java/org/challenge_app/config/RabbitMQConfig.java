package org.challenge_app.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.messaging.exchange}")
    private String exchange;

    @Value("${app.messaging.queue}")
    private String queue;

    @Value("${app.messaging.routing-key}")
    private String routingKey;

    @Bean
    public TopicExchange customerExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue customerQueue() {
        return new Queue(queue);
    }

    @Bean
    public Binding binding(Queue customerQueue, TopicExchange customerExchange) {
        return BindingBuilder.bind(customerQueue).to(customerExchange).with(routingKey);
    }
}
