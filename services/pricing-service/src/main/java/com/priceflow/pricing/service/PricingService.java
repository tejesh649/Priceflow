package com.priceflow.pricing.service;

import com.priceflow.events.costrequest.CostRequestApprovedEvent;
import com.priceflow.pricing.entity.ItemCost;
import com.priceflow.pricing.enums.ItemCostStatus;
import com.priceflow.pricing.repository.ItemCostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingService {

    private final ItemCostRepository itemCostRepository;

    public Mono<ItemCost> processApprovedCost(CostRequestApprovedEvent event) {

        return itemCostRepository.existsByEventId(event.getEventId().toString())
                .flatMap(exists -> {
                    if (exists) {
                        log.info(
                                "Ignoring duplicate COST_REQUEST_APPROVED event: eventId={}, requestId={}",
                                event.getEventId(),
                                event.getRequestId()
                        );

                        return Mono.empty();
                    }

                    Instant now = Instant.now();

                    ItemCostStatus status =
                            event.getEffectiveDate().isAfter(LocalDate.now())
                                    ? ItemCostStatus.SCHEDULED
                                    : ItemCostStatus.ACTIVE;

                    ItemCost itemCost = ItemCost.builder()
                            .requestId(event.getRequestId().toString())
                            .vendorId(event.getVendorId().toString())
                            .itemNumber(event.getItemNumber())
                            .cost(event.getApprovedCost())
                            .effectiveDate(event.getEffectiveDate())
                            .status(status)
                            .eventId(event.getEventId().toString())
                            .createdAt(now)
                            .updatedAt(now)
                            .build();

                    return itemCostRepository.save(itemCost)
                            .doOnSuccess(saved ->
                                    log.info(
                                            "Persisted approved cost: requestId={}, itemNumber={}, cost={}, effectiveDate={}, status={}",
                                            saved.getRequestId(),
                                            saved.getItemNumber(),
                                            saved.getCost(),
                                            saved.getEffectiveDate(),
                                            saved.getStatus()
                                    )
                            );
                });
    }
}