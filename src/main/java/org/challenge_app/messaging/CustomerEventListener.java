package org.challenge_app.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.challenge_app.dto.CustomerResponse;
import org.challenge_app.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerEventListener {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public CustomerEventListener(ObjectMapper objectMapper, JavaMailSender mailSender, EmailService emailService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${app.messaging.queue}")
    public void handleCustomerCreated(String message) {
        try{
            CustomerResponse customerResponse = objectMapper.readValue(message, CustomerResponse.class);
            log.info("Received async customer event: {}", customerResponse);

            String subject = "Nuevo cliente creado: " + customerResponse.getFullName();
            String body = String.format("""
                    Se ha creado un nuevo cliente:

                    Nombre: %s
                    Edad: %d
                    Fecha de nacimiento: %s

                    Gracias.
                    """, customerResponse.getFullName(), customerResponse.getAge(), customerResponse.getDateOfBirth());

            emailService.sendCustomerCreatedEmail("destinatario@example.com", subject, body);

        }catch (Exception e){
            log.error("Error parsing message: {}", message, e);
        }
    }
}