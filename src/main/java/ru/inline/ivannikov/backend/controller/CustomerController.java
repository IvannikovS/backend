package ru.inline.ivannikov.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.inline.ivannikov.backend.dto.CustomerDto;
import ru.inline.ivannikov.backend.dto.LotDto;
import ru.inline.ivannikov.backend.filter.CustomerFilter;
import ru.inline.ivannikov.backend.service.CustomerService;
import ru.inline.ivannikov.backend.service.LotService;

import java.util.List;


@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService service;
    private final LotService lotService;

    @GetMapping("/all")
    public List<CustomerDto> getAll() {
        return service.getAll();
    }

    @GetMapping
    public ResponseEntity<Page<CustomerDto>> list(
            CustomerFilter filter,
            @PageableDefault(size = 20) Pageable  pageable
    ) {
        return ResponseEntity.ok(service.findCustomers(filter, pageable));
    }

    @GetMapping("/{code}")
    public ResponseEntity<CustomerDto> getCustomerByCode(@PathVariable String code){
        return ResponseEntity.ok(service.getByCode(code));
    }

    @PostMapping
    public ResponseEntity<CustomerDto> create(@Valid @RequestBody CustomerDto dto){
        CustomerDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{code}")
    public ResponseEntity<CustomerDto> update(
            @PathVariable String code,
            @Valid @RequestBody CustomerDto dto
    ) {
        return ResponseEntity.ok(service.update(code, dto));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code){
        service.delete(code);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{code}/cascade")
    @Operation(summary = "Каскадное удаление контрагента со всеми связанными данными")
    public ResponseEntity<Void> deleteCascade(@PathVariable String code) {
        service.deleteCascade(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{code}/parent")
    public ResponseEntity<CustomerDto> getParent(@PathVariable String code) {
        return ResponseEntity.ok(service.getParentCustomer(code));
    }

    @GetMapping("/{code}/children")
    public ResponseEntity<List<CustomerDto>> getChildren(@PathVariable String code) {
        return ResponseEntity.ok(service.getChildrenCustomers(code));
    }

    @GetMapping("/{code}/lots")
    public ResponseEntity<List<LotDto>> getCustomerLots(@PathVariable String code) {
        return ResponseEntity.ok(lotService.getLotsByCustomerCode(code));
    }
}
