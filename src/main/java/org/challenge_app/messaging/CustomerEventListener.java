package org.challenge_app.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.challenge_app.dto.CustomerResponse;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerEventListener {

    private final ObjectMapper objectMapper;

    public CustomerEventListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${app.messaging.queue}")
    public void handleCustomerCreated(String message) {
        try{
            CustomerResponse customerResponse = objectMapper.readValue(message, CustomerResponse.class);
            log.info("Received async customer event: {}", customerResponse);
            // resto de procesos: enviar email, guardar log, replicar datos, etc.

        }catch (Exception e){
            log.error("Error parsing message: {}", message, e);
        }
    }
}