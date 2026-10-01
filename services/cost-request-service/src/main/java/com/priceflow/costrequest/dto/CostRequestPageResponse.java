package com.priceflow.costrequest.dto;

import java.util.List;

public record CostRequestPageResponse(
        List<CostRequestResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}