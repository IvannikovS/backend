package ru.inline.ivannikov.backend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inline.ivannikov.backend.dto.CustomerDto;
import ru.inline.ivannikov.backend.dto.LotDto;
import ru.inline.ivannikov.backend.filter.LotFilter;
import ru.inline.ivannikov.backend.mapper.CustomerMapper;
import ru.inline.ivannikov.backend.mapper.LotMapper;
import ru.inline.ivannikov.backend.model.Lot;
import ru.inline.ivannikov.backend.repository.CustomerRepository;
import ru.inline.ivannikov.backend.repository.LotRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LotService {
    private final LotRepository repository;
    private final LotMapper mapper;
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public List<LotDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public LotDto getById(Long id) {
        return mapper.toDto(repository.findById(id));
    }

    public Page<LotDto> findLots(LotFilter filter,  Pageable pageable) {
        return repository.findLots(filter, pageable)
                .map(mapper::toDto);
    }

    @Transactional
    public LotDto create(LotDto dto) {
        if (!customerRepository.existsByCustomerCode(dto.getCustomerCode())) {
            throw new EntityNotFoundException("Customer not found: " + dto.getCustomerCode());
        }

        Lot entity = mapper.toEntity(dto);
        Lot saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public LotDto update(Long id, LotDto dto) {
        Lot entity = repository.findById(id);
        mapper.updateLotFromDto(dto, entity);
        Lot updated = repository.save(entity);
        return mapper.toDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<LotDto> getLotsByCustomerCode(String customerCode) {
        if (!customerRepository.existsByCustomerCode(customerCode)) {
            throw new EntityNotFoundException("Customer not found: " + customerCode);
        }

        return repository.findByCustomerCode(customerCode)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public CustomerDto getCustomerForLot(Long id) {
        return repository.getCustomerForLot(id)
                .map(customerMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found for lot: " + id));
    }
}
