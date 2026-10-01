package com.priceflow.costrequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CostRequestCreateRequest(

        @NotBlank
        String vendorId,

        @NotNull
        @Positive
        Long itemNumber,

        @NotNull
        @Positive
        BigDecimal proposedCost,

        @NotBlank
        @Size(max = 1000)
        String reason
) {
}