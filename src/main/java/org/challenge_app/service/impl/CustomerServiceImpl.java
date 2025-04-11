package org.challenge_app.service.impl;

import lombok.RequiredArgsConstructor;
import org.challenge_app.dto.CustomerMetricsResponse;
import org.challenge_app.dto.CustomerResponse;
import org.challenge_app.dto.CustomerRequest;
import org.challenge_app.dto.mapper.CustomerMapper;
import org.challenge_app.exception.ResourceNotFoundException;
import org.challenge_app.messaging.CustomerEventPublisher;
import org.challenge_app.model.Customer;
import org.challenge_app.repository.CustomerRepository;
import org.challenge_app.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerEventPublisher customerEventPublisher;

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest customerRequest) {
        Customer customer = customerMapper.toEntity(customerRequest);
        Customer savedCustomer = customerRepository.save(customer);
        CustomerResponse customerResponse = customerMapper.toResponse(savedCustomer);
        customerEventPublisher.publishCustomerCreatedEvent(customerResponse);
        return customerResponse;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public CustomerMetricsResponse getCustomerMetrics() {
        List<Integer> ages = customerRepository.findAllAges();

        if (ages.isEmpty()) {
            return CustomerMetricsResponse.builder()
                    .totalCustomers(0)
                    .averageAge(0)
                    .ageStandardDeviation(0)
                    .minAge(0)
                    .maxAge(0)
                    .build();
        }

        double average = ages.stream().mapToInt(Integer::intValue).average().orElse(0);
        double variance = ages.stream()
                .mapToDouble(age -> Math.pow(age - average, 2))
                .average().orElse(0);
        double standardDeviation = Math.sqrt(variance);

        return CustomerMetricsResponse.builder()
                .totalCustomers(ages.size())
                .averageAge(Math.round(average * 100.0) / 100.0)
                .ageStandardDeviation(Math.round(standardDeviation * 100.0) / 100.0)
                .minAge(ages.stream().mapToInt(Integer::intValue).min().orElse(0))
                .maxAge(ages.stream().mapToInt(Integer::intValue).max().orElse(0))
                .build();
    }
}
