package com.priceflow.costrequest.dto;

import jakarta.validation.constraints.NotBlank;

public record ApproveCostRequestRequest(

        @NotBlank
        String approverId

) {
}