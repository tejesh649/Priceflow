package com.priceflow.pricing.service;

import com.priceflow.pricing.entity.ItemCost;
import com.priceflow.pricing.enums.ItemCostStatus;
import com.priceflow.pricing.repository.ItemCostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class CostActivationService {

    private static final ZoneId ACTIVATION_ZONE =
            ZoneId.of("America/New_York");

    private final ItemCostRepository itemCostRepository;
    private final TransactionalOperator transactionalOperator;

    public Flux<ItemCost> activateEligibleCosts() {

        LocalDate today = LocalDate.now(ACTIVATION_ZONE);

        log.info("Starting scheduled cost activation for date={}", today);

        return itemCostRepository
                .findByStatusAndEffectiveDateLessThanEqual(
                        ItemCostStatus.SCHEDULED,
                        today
                )
                .concatMap(this::activateCost);
    }

    private Mono<ItemCost> activateCost(ItemCost scheduledCost) {

        return itemCostRepository
                .findByVendorIdAndItemNumberAndStatus(
                        scheduledCost.getVendorId(),
                        scheduledCost.getItemNumber(),
                        ItemCostStatus.ACTIVE
                )
                .flatMap(activeCost -> expire(activeCost)
                        .then(activate(scheduledCost)))
                .switchIfEmpty(Mono.defer(() -> activate(scheduledCost)))
                .as(transactionalOperator::transactional);
    }

    private Mono<ItemCost> expire(ItemCost activeCost) {

        activeCost.setStatus(ItemCostStatus.EXPIRED);
        activeCost.setUpdatedAt(Instant.now());

        return itemCostRepository.save(activeCost)
                .doOnSuccess(saved ->
                        log.info(
                                "Expired previous cost: requestId={}, vendorId={}, itemNumber={}, cost={}",
                                saved.getRequestId(),
                                saved.getVendorId(),
                                saved.getItemNumber(),
                                saved.getCost()
                        )
                );
    }

    private Mono<ItemCost> activate(ItemCost scheduledCost) {

        scheduledCost.setStatus(ItemCostStatus.ACTIVE);
        scheduledCost.setUpdatedAt(Instant.now());

        return itemCostRepository.save(scheduledCost)
                .doOnSuccess(saved ->
                        log.info(
                                "Activated cost: requestId={}, vendorId={}, itemNumber={}, cost={}, effectiveDate={}",
                                saved.getRequestId(),
                                saved.getVendorId(),
                                saved.getItemNumber(),
                                saved.getCost(),
                                saved.getEffectiveDate()
                        )
                );
    }
}