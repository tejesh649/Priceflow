package com.priceflow.costrequest.dto;

import com.priceflow.costrequest.enums.CostRequestStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CostRequestResponse(
        String requestId,
        String vendorId,
        Long itemNumber,
        BigDecimal proposedCost,
        String reason,
        CostRequestStatus status,
        LocalDate effectiveDate,
        String approvedBy,
        Instant approvedAt,
        String rejectedBy,
        Instant rejectedAt,
        String rejectionReason,
        Instant createdAt,
        Instant updatedAt
) {
}