package ru.inline.ivannikov.backend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inline.ivannikov.backend.dto.CustomerDto;
import ru.inline.ivannikov.backend.filter.CustomerFilter;
import ru.inline.ivannikov.backend.mapper.CustomerMapper;
import ru.inline.ivannikov.backend.model.Customer;
import ru.inline.ivannikov.backend.repository.CustomerRepository;
import ru.inline.ivannikov.backend.repository.LotRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final LotRepository lotRepository;

    public List<CustomerDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public CustomerDto getByCode(String customerCode) {
        return mapper.toDto(repository.findByCustomerCode(customerCode));
    }

    public Page<CustomerDto> findCustomers(CustomerFilter filter, Pageable pageable) {
        return repository.findCustomers(filter, pageable)
                .map(mapper::toDto);
    }

    @Transactional
    public CustomerDto create(CustomerDto customerDto) {
        Customer entity = mapper.toEntity(customerDto);
        Customer saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public CustomerDto update(String customerCode, CustomerDto customerDto) {
        Customer entity = repository.findByCustomerCode(customerCode);
        mapper.updateCustomerFromDto(customerDto, entity);
        Customer updated = repository.save(entity);
        return mapper.toDto(updated);
    }

    @Transactional
    public void delete(String customerCode) {
        if (!lotRepository.findByCustomerCode(customerCode).isEmpty()) {
            throw new IllegalStateException("Cannot delete customer with existing lots");
        }
        repository.deleteCustomerByCode(customerCode);
    }

    @Transactional
    public void deleteCascade(String customerCode) {
        if (!repository.existsByCustomerCode(customerCode)) {
            throw new EntityNotFoundException("Customer not found: " + customerCode);
        }
        repository.deleteCustomerCascade(customerCode);
    }

    public CustomerDto getParentCustomer(String childCode) {
        return repository.getParentCustomer(childCode)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Parent not found for customer: " + childCode));
    }

    public List<CustomerDto> getChildrenCustomers(String parentCode) {
        return repository.getChildrenCustomers(parentCode)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
