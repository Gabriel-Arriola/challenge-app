package org.challenge_app.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.challenge_app.dto.CustomerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CustomerEventPublisher customerEventPublisher;

    private CustomerResponse customer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Simular inyección de @Value
        var exchangeField = CustomerEventPublisher.class.getDeclaredField("exchange");
        exchangeField.setAccessible(true);
        exchangeField.set(customerEventPublisher, "test-exchange");

        var routingField = CustomerEventPublisher.class.getDeclaredField("routingKey");
        routingField.setAccessible(true);
        routingField.set(customerEventPublisher, "test.routing.key");

        customer = CustomerResponse.builder()
                .id(1L)
                .firstName("Lucía")
                .lastName("González")
                .age(30)
                .dateOfBirth(LocalDate.of(1994, 3, 12))
                .fullName("Lucía González")
                .estimatedLifeExpectancy(LocalDate.of(2074, 3, 12))
                .build();
    }

    @Test
    void publishCustomerCreatedEvent_shouldSendMessage() throws JsonProcessingException {
        String json = "{\"id\":1,\"firstName\":\"Lucía\"}";
        when(objectMapper.writeValueAsString(customer)).thenReturn(json);

        customerEventPublisher.publishCustomerCreatedEvent(customer);

        verify(rabbitTemplate).convertAndSend("test-exchange", "test.routing.key", json);
    }

    @Test
    void publishCustomerCreatedEvent_shouldThrowException_onSerializationError() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(customer)).thenThrow(new JsonProcessingException("ERROR") {});

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerEventPublisher.publishCustomerCreatedEvent(customer)
        );

        assertTrue(ex.getMessage().contains("Error serializing customer"));
    }
}
