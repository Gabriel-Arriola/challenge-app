package org.challenge_app.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.challenge_app.dto.CustomerResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.messaging.exchange}")
    private String exchange;

    @Value("${app.messaging.routing-key}")
    private String routingKey;


    public void publishCustomerCreatedEvent(CustomerResponse customer) {
        try{
            String message = objectMapper.writeValueAsString(customer);
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
        } catch (JsonProcessingException e){
            throw new RuntimeException("Error serializing customer", e);
        }

    }
}
