package org.challenge_app.mapper;

import org.challenge_app.dto.CustomerRequest;
import org.challenge_app.dto.CustomerResponse;
import org.challenge_app.dto.mapper.CustomerMapper;
import org.challenge_app.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomerMapperTest {

    private CustomerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CustomerMapper.class);
    }

    @Test
    void toEntity_shouldMapFromRequestToCustomer() {
        CustomerRequest request = CustomerRequest.builder()
                .firstName("Mateo")
                .lastName("Ramírez")
                .age(40)
                .dateOfBirth(LocalDate.of(1984, 3, 15))
                .build();

        Customer customer = mapper.toEntity(request);

        assertNotNull(customer);
        assertEquals("Mateo", customer.getFirstName());
        assertEquals("Ramírez", customer.getLastName());
        assertEquals(40, customer.getAge());
        assertEquals(LocalDate.of(1984, 3, 15), customer.getDateOfBirth());
    }

    @Test
    void toResponse_shouldMapFromCustomerToResponseWithDerivedFields() {
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("Lucía")
                .lastName("Fernández")
                .age(27)
                .dateOfBirth(LocalDate.of(1997, 5, 10))
                .build();

        CustomerResponse response = mapper.toResponse(customer);

        assertNotNull(response);
        assertEquals("Lucía", response.getFirstName());
        assertEquals("Lucía Fernández", response.getFullName());
        assertEquals(LocalDate.of(2077, 5, 10), response.getEstimatedLifeExpectancy());
    }
}