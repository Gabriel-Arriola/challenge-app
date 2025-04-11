package org.challenge_app.controller;

import org.challenge_app.dto.CustomerRequest;
import org.challenge_app.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/customers";
        customerRepository.deleteAll(); // Limpieza antes de cada test
    }

    private String obtenerTokenValido() {
        String url = "http://localhost:" + port + "/api/auth/token?username=admin&password=password";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
        return "Bearer " + Objects.requireNonNull(response.getBody()).get("token").toString();
    }

    @Test
    void createCustomer_shouldReturnCreatedCustomer() {
        CustomerRequest request = CustomerRequest.builder()
                .firstName("Laura")
                .lastName("Gomez")
                .age(28)
                .dateOfBirth(LocalDate.of(1996, 1, 1))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", obtenerTokenValido());

        HttpEntity<CustomerRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl, entity, Map.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Laura", Objects.requireNonNull(response.getBody()).get("firstName"));
    }

    @Test
    void getAllCustomers_shouldReturnList() {
        createCustomer_shouldReturnCreatedCustomer();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", obtenerTokenValido());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map[]> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                entity,
                Map[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).length >= 1);
    }

    @Test
    void getCustomerById_shouldReturnCustomer() {
        CustomerRequest request = CustomerRequest.builder()
                .firstName("Lucía")
                .lastName("Ramírez")
                .age(33)
                .dateOfBirth(LocalDate.of(1991, 5, 15))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", obtenerTokenValido());

        HttpEntity<CustomerRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> postResponse = restTemplate.postForEntity(baseUrl, entity, Map.class);
        long id = Long.parseLong(Objects.requireNonNull(postResponse.getBody()).get("id").toString());

        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/" + id,
                HttpMethod.GET,
                getEntity,
                Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Lucía", Objects.requireNonNull(response.getBody()).get("firstName"));
    }

    @Test
    void getCustomerMetrics_shouldReturnStats() {
        createCustomer_shouldReturnCreatedCustomer();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", obtenerTokenValido());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/metrics",
                HttpMethod.GET,
                entity,
                Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((Integer) Objects.requireNonNull(response.getBody()).get("totalCustomers")) >= 1);
    }
}