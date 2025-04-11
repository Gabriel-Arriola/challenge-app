package org.challenge_app.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.challenge_app.dto.CustomerMetricsResponse;
import org.challenge_app.dto.CustomerRequest;
import org.challenge_app.dto.CustomerResponse;
import org.challenge_app.dto.mapper.CustomerMapper;
import org.challenge_app.exception.ResourceNotFoundException;
import org.challenge_app.messaging.CustomerEventPublisher;
import org.challenge_app.model.Customer;
import org.challenge_app.repository.CustomerRepository;
import org.challenge_app.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock private CustomerMapper customerMapper;
    @Mock private CustomerEventPublisher customerEventPublisher;
    @Mock private MeterRegistry meterRegistry;
    @Mock private Counter counter;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerRequest customerRequest;
    private Customer customer;
    private CustomerResponse customerResponse;

    @BeforeEach
    void setUp() {
        customerRequest = CustomerRequest.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .age(30)
                .dateOfBirth(LocalDate.of(1994, 1, 1))
                .build();

        customer = Customer.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .age(30)
                .dateOfBirth(LocalDate.of(1994, 1, 1))
                .build();

        customerResponse = CustomerResponse.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .age(30)
                .dateOfBirth(LocalDate.of(1994, 1, 1))
                .fullName("Juan Pérez")
                .estimatedLifeExpectancy(LocalDate.of(2074, 1, 1))
                .build();
    }

    @Test
    void createCustomer_shouldSaveAndReturnResponse() {
        when(customerMapper.toEntity(customerRequest)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);
        when(meterRegistry.counter("customer.created")).thenReturn(counter);

        CustomerResponse result = customerService.createCustomer(customerRequest);

        assertNotNull(result);
        assertEquals("Juan", result.getFirstName());
        assertEquals("Pérez", result.getLastName());

        verify(customerRepository).save(customer);
        verify(customerEventPublisher).publishCustomerCreatedEvent(result);
        verify(counter).increment();
    }

    @Test
    void getAllCustomers_shouldReturnMappedList() {
        List<Customer> customers = List.of(customer);
        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        List<CustomerResponse> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getFirstName());
        verify(customerRepository).findAll();
    }

    @Test
    void getCustomerById_whenFound_shouldReturnResponse() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        CustomerResponse result = customerService.getCustomerById(1L);

        assertEquals(30, result.getAge());
        verify(customerRepository).findById(1L);
    }

    @Test
    void getCustomerById_whenNotFound_shouldThrowException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(1L));
        verify(customerRepository).findById(1L);
    }

    @Test
    void getCustomerMetrics_shouldReturnValidStats() {
        List<Integer> ages = Arrays.asList(20, 30, 40);
        when(customerRepository.findAllAges()).thenReturn(ages);

        CustomerMetricsResponse result = customerService.getCustomerMetrics();

        assertEquals(3, result.getTotalCustomers());
        assertEquals(30.0, result.getAverageAge());
        assertEquals(8.16, result.getAgeStandardDeviation(), 0.01); // Aprox. desviación típica
        assertEquals(20, result.getMinAge());
        assertEquals(40, result.getMaxAge());
    }

    @Test
    void getCustomerMetrics_whenNoCustomers_shouldReturnZeros() {
        when(customerRepository.findAllAges()).thenReturn(List.of());

        CustomerMetricsResponse result = customerService.getCustomerMetrics();

        assertEquals(0, result.getTotalCustomers());
        assertEquals(0, result.getAverageAge());
        assertEquals(0, result.getAgeStandardDeviation());
    }
}