package org.challenge_app.service;

import org.challenge_app.dto.CustomerResponse;
import org.challenge_app.dto.CustomerMetricsResponse;
import org.challenge_app.dto.CustomerRequest;

import java.util.List;

public interface CustomerService {
    public CustomerResponse createCustomer(CustomerRequest customerRequest);
    public List<CustomerResponse> getAllCustomers();
    public CustomerResponse getCustomerById(Long id);
    public CustomerMetricsResponse getCustomerMetrics();
}
