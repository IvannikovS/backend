package ru.inline.ivannikov.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ru.inline.ivannikov.backend.model.types.CurrencyCode;
import ru.inline.ivannikov.backend.model.types.NdsRate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotDto {
    private Long lotId;

    @NotBlank(message = "Lot name must not be blank")
    private String lotName;

    @NotBlank(message = "Customer code must not be blank")
    private String customerCode;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Currency is required")
    private CurrencyCode currencyCode;

    @NotNull(message = "NDS rate is required")
    private String ndsRate;

    private String placeDelivery;

    @NotNull(message = "Delivery date is required")
    private LocalDateTime dateDelivery;
}
