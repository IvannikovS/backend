package ru.inline.ivannikov.backend.controller;

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
import ru.inline.ivannikov.backend.filter.LotFilter;
import ru.inline.ivannikov.backend.service.LotService;

import java.util.List;


@RestController
@RequestMapping("/api/lots")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class LotController {
    private final LotService service;

    @GetMapping("/all")
    public List<LotDto> findAll() {
        return service.getAll();
    }

    @GetMapping
    public ResponseEntity<Page<LotDto>> list(
            LotFilter filter,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(service.findLots(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LotDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<LotDto> create(@Valid @RequestBody LotDto dto) {
        LotDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LotDto> update(
            @PathVariable Long id,
            @Valid @RequestBody LotDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/customer")
    public ResponseEntity<CustomerDto> getLotCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCustomerForLot(id));
    }

}
