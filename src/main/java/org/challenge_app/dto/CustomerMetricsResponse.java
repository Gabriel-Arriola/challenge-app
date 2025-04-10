package org.challenge_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerMetricsResponse {
    private long totalCustomers;
    private double averageAge;
    private double ageStandardDeviation;
    private Integer minAge;
    private Integer maxAge;
}