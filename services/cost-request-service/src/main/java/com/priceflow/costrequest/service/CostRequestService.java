package com.priceflow.costrequest.service;

import com.priceflow.costrequest.dto.*;
import com.priceflow.costrequest.entity.CostRequest;
import com.priceflow.costrequest.enums.CostRequestStatus;
import com.priceflow.costrequest.event.CostRequestEventMapper;
import com.priceflow.costrequest.event.CostRequestEventPublisher;
import com.priceflow.costrequest.exception.CostRequestNotFoundException;
import com.priceflow.costrequest.exception.InvalidCostRequestStatusException;
import com.priceflow.costrequest.repository.CostRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CostRequestService {

    private final CostRequestRepository costRequestRepository;
    private final CostRequestEventMapper costRequestEventMapper;
    private final CostRequestEventPublisher costRequestEventPublisher;

    public Mono<CostRequestResponse> createCostRequest(
            CostRequestCreateRequest request) {

        Instant now = Instant.now();

        CostRequest costRequest = CostRequest.builder()
                .requestId(generateRequestId())
                .vendorId(request.vendorId())
                .itemNumber(request.itemNumber())
                .proposedCost(request.proposedCost())
                .reason(request.reason())
                .status(CostRequestStatus.PENDING_APPROVAL)
                .createdAt(now)
                .updatedAt(now)
                .build();

        log.info(
                "Saving cost request requestId={}, vendorId={}, itemNumber={}, status={}",
                costRequest.getRequestId(),
                costRequest.getVendorId(),
                costRequest.getItemNumber(),
                costRequest.getStatus()
        );

        return costRequestRepository.save(costRequest)
                .doOnSuccess(saved ->
                        log.info(
                                "Successfully saved cost request requestId={}",
                                saved.getRequestId()
                        )
                )
                .doOnError(error ->
                        log.error(
                                "Failed to save cost request requestId={}",
                                costRequest.getRequestId(),
                                error
                        )
                )
                .map(this::toResponse);
    }

    private String generateRequestId() {
        return "CR-" + Instant.now().toEpochMilli();
    }

    private CostRequestResponse toResponse(CostRequest costRequest) {
        return new CostRequestResponse(
                costRequest.getRequestId(),
                costRequest.getVendorId(),
                costRequest.getItemNumber(),
                costRequest.getProposedCost(),
                costRequest.getReason(),
                costRequest.getStatus(),
                costRequest.getEffectiveDate(),
                costRequest.getApprovedBy(),
                costRequest.getApprovedAt(),
                costRequest.getRejectedBy(),
                costRequest.getRejectedAt(),
                costRequest.getRejectionReason(),
                costRequest.getCreatedAt(),
                costRequest.getUpdatedAt()
        );
    }

    public Mono<CostRequestResponse> getCostRequestById(String requestId) {
        return costRequestRepository.findById(requestId)
                .switchIfEmpty(Mono.error(
                        new CostRequestNotFoundException(requestId)
                ))
                .map(this::toResponse);
    }

    public Mono<CostRequestPageResponse> getCostRequests(
            CostRequestStatus status,
            int page,
            int size) {

        long offset = (long) page * size;

        Flux<CostRequest> requests;
        Mono<Long> count;

        if (status != null) {
            requests = costRequestRepository.findByStatusPaged(
                    status,
                    size,
                    offset
            );

            count = costRequestRepository.countByStatus(status);
        } else {
            requests = costRequestRepository.findAllPaged(
                    size,
                    offset
            );

            count = costRequestRepository.count();
        }

        return Mono.zip(
                requests.map(this::toResponse).collectList(),
                count
        ).map(tuple -> {

            List<CostRequestResponse> content = tuple.getT1();
            long totalElements = tuple.getT2();

            int totalPages =
                    (int) Math.ceil((double) totalElements / size);

            return new CostRequestPageResponse(
                    content,
                    page,
                    size,
                    totalElements,
                    totalPages
            );
        });
    }

    public Mono<CostRequestResponse> approveCostRequest(
            String requestId,
            ApproveCostRequestRequest request) {

        return costRequestRepository.findById(requestId)
                .switchIfEmpty(Mono.error(
                        new CostRequestNotFoundException(requestId)
                ))
                .flatMap(costRequest -> {

                    if (costRequest.getStatus() != CostRequestStatus.PENDING_APPROVAL) {
                        return Mono.error(
                                new InvalidCostRequestStatusException(
                                        "Cost request cannot be approved from status: "
                                                + costRequest.getStatus()
                                )
                        );
                    }

                    Instant now = Instant.now();

                    costRequest.setStatus(CostRequestStatus.APPROVED);
                    costRequest.setApprovedBy(request.approverId());
                    costRequest.setApprovedAt(now);
                    costRequest.setEffectiveDate(
                            LocalDate.now(ZoneOffset.UTC).plusDays(1)
                    );
                    costRequest.setUpdatedAt(now);

                    log.info(
                            "Approving cost request requestId={}, approverId={}, effectiveDate={}",
                            requestId,
                            request.approverId(),
                            costRequest.getEffectiveDate()
                    );

                    return costRequestRepository.save(costRequest)
                            .flatMap(savedCostRequest -> {

                                var event =
                                        costRequestEventMapper.toApprovedEvent(savedCostRequest);

                                return costRequestEventPublisher
                                        .publishApproved(event)
                                        .thenReturn(savedCostRequest);
                            });
                })
                .doOnSuccess(saved ->
                        log.info(
                                "Successfully approved cost request requestId={}",
                                saved.getRequestId()
                        )
                )
                .doOnError(error ->
                        log.error(
                                "Failed to approve cost request requestId={}",
                                requestId,
                                error
                        )
                )
                .map(this::toResponse);
    }

    public Mono<CostRequestResponse> rejectCostRequest(
            String requestId,
            RejectCostRequestRequest request) {

        return costRequestRepository.findById(requestId)
                .switchIfEmpty(Mono.error(
                        new CostRequestNotFoundException(requestId)
                ))
                .flatMap(costRequest -> {

                    if (costRequest.getStatus() != CostRequestStatus.PENDING_APPROVAL) {
                        return Mono.error(
                                new InvalidCostRequestStatusException(
                                        "Cost request cannot be rejected from status: "
                                                + costRequest.getStatus()
                                )
                        );
                    }

                    Instant now = Instant.now();

                    costRequest.setStatus(CostRequestStatus.REJECTED);
                    costRequest.setRejectedBy(request.approverId());
                    costRequest.setRejectedAt(now);
                    costRequest.setRejectionReason(request.rejectionReason());
                    costRequest.setUpdatedAt(now);

                    log.info(
                            "Rejecting cost request requestId={}, approverId={}",
                            requestId,
                            request.approverId()
                    );

                    return costRequestRepository.save(costRequest);
                })
                .doOnSuccess(saved ->
                        log.info(
                                "Successfully rejected cost request requestId={}",
                                saved.getRequestId()
                        )
                )
                .doOnError(error ->
                        log.error(
                                "Failed to reject cost request requestId={}",
                                requestId,
                                error
                        )
                )
                .map(this::toResponse);
    }

}