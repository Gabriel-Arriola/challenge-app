package org.challenge_app.dto.mapper;

import org.challenge_app.dto.CustomerRequest;
import org.challenge_app.dto.CustomerResponse;
import org.challenge_app.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Customer toEntity(CustomerRequest customerRequest);

    @Mapping(target = "fullName", source = ".", qualifiedByName = "buildFullName")
    @Mapping(target = "estimatedLifeExpectancy", source = ".", qualifiedByName = "calculateLifeExpectancy")
    CustomerResponse toResponse(Customer customer);

    @Named("buildFullName")
    default String buildFullName(Customer customer) {
        return customer.getFirstName() + " " + customer.getLastName();
    }

    @Named("calculateLifeExpectancy")
    default LocalDate calculateLifeExpectancy(Customer customer) {
        return customer.getEstimatedLifeExpectancy();
    }
}
