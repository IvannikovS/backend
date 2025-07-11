package ru.inline.ivannikov.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.inline.ivannikov.backend.dto.CustomerDto;
import ru.inline.ivannikov.backend.model.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDto toDto(Customer customer);

    Customer toEntity(CustomerDto customerDto);

    @Mapping(target = "customerCode", ignore = true)
    void updateCustomerFromDto(CustomerDto dto, @MappingTarget Customer entity);
}
