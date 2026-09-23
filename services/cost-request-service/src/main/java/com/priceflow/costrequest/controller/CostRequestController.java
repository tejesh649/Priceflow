package com.priceflow.costrequest.controller;

import com.priceflow.costrequest.dto.*;
import com.priceflow.costrequest.enums.CostRequestStatus;
import com.priceflow.costrequest.service.CostRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/cost-requests")
@RequiredArgsConstructor
public class CostRequestController {

    private final CostRequestService costRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CostRequestResponse> createCostRequest(
            @Valid @RequestBody CostRequestCreateRequest request) {

        log.info(
                "Received cost request submission for vendorId={}, itemNumber={}",
                request.vendorId(),
                request.itemNumber()
        );

        return costRequestService.createCostRequest(request);
    }
    @GetMapping("/{requestId}")
    public Mono<CostRequestResponse> getCostRequestById(
            @PathVariable String requestId) {

        log.info("Fetching cost request requestId={}", requestId);

        return costRequestService.getCostRequestById(requestId);
    }

    @GetMapping
    public Mono<CostRequestPageResponse> getCostRequests(
            @RequestParam(required = false) CostRequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info(
                "Fetching cost requests status={}, page={}, size={}",
                status,
                page,
                size
        );

        return costRequestService.getCostRequests(
                status,
                page,
                size
        );
    }

    @PatchMapping("/{requestId}/approve")
    public Mono<CostRequestResponse> approveCostRequest(
            @PathVariable String requestId,
            @Valid @RequestBody ApproveCostRequestRequest request) {

        log.info(
                "Received approval request requestId={}, approverId={}",
                requestId,
                request.approverId()
        );

        return costRequestService.approveCostRequest(requestId, request);
    }

    @PatchMapping("/{requestId}/reject")
    public Mono<CostRequestResponse> rejectCostRequest(
            @PathVariable String requestId,
            @Valid @RequestBody RejectCostRequestRequest request) {

        log.info(
                "Received rejection request requestId={}, approverId={}",
                requestId,
                request.approverId()
        );

        return costRequestService.rejectCostRequest(requestId, request);
    }

}