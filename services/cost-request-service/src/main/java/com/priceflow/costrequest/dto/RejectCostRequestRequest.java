package com.priceflow.costrequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectCostRequestRequest(

        @NotBlank
        String approverId,

        @NotBlank
        @Size(max = 1000)
        String rejectionReason

) {
}