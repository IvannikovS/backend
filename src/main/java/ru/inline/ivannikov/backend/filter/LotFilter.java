package ru.inline.ivannikov.backend.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotFilter {
    private String lotName;
    private String customerCode;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
