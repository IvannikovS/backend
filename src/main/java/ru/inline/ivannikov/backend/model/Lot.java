package ru.inline.ivannikov.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.inline.ivannikov.backend.model.types.CurrencyCode;
import ru.inline.ivannikov.backend.model.types.NdsRate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lot {
    private Long lotId;
    private String lotName;
    private String customerCode;
    private BigDecimal price;
    private CurrencyCode currencyCode;
    private String ndsRate;
    private String placeDelivery;
    private LocalDateTime dateDelivery;
}
