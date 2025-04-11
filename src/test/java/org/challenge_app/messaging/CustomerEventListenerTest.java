package org.challenge_app.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.challenge_app.dto.CustomerResponse;
import org.challenge_app.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerEventListenerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private CustomerEventListener listener;

    private CustomerResponse customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new CustomerEventListener(objectMapper, mailSender, emailService);

        customer = CustomerResponse.builder()
                .id(1L)
                .firstName("Sofía")
                .lastName("Ruiz")
                .age(35)
                .dateOfBirth(LocalDate.of(1989, 2, 20))
                .fullName("Sofía Ruiz")
                .estimatedLifeExpectancy(LocalDate.of(2069, 2, 20))
                .build();
    }

    @Test
    void handleCustomerCreated_shouldParseMessageAndSendEmail() throws Exception {
        String message = "{\"id\":1,\"firstName\":\"Sofía\",\"lastName\":\"Ruiz\",\"age\":35,\"dateOfBirth\":\"1989-02-20\"}";

        when(objectMapper.readValue(message, CustomerResponse.class)).thenReturn(customer);

        listener.handleCustomerCreated(message);

        verify(objectMapper).readValue(message, CustomerResponse.class);
        verify(emailService).sendCustomerCreatedEmail(
                eq("destinatario@example.com"),
                contains("Nuevo cliente creado"),
                contains("Sofía Ruiz")
        );
    }

    @Test
    void handleCustomerCreated_shouldLogError_onParseException() throws Exception {
        String badMessage = "{malformed-json}";
        when(objectMapper.readValue(badMessage, CustomerResponse.class)).thenThrow(new JsonProcessingException("error") {});

        listener.handleCustomerCreated(badMessage);

        // No debe llamar al servicio de email
        verify(emailService, never()).sendCustomerCreatedEmail(any(), any(), any());
    }
}