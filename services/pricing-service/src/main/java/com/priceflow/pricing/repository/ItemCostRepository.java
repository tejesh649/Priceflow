package com.priceflow.pricing.repository;

import com.priceflow.pricing.entity.ItemCost;
import com.priceflow.pricing.enums.ItemCostStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ItemCostRepository
        extends ReactiveCrudRepository<ItemCost, Long> {

    Mono<Boolean> existsByEventId(String eventId);

    Mono<ItemCost> findByRequestId(String requestId);

    Flux<ItemCost> findByStatusAndEffectiveDateLessThanEqual(
            ItemCostStatus status,
            LocalDate effectiveDate
    );

    Mono<ItemCost> findByVendorIdAndItemNumberAndStatus(
            String vendorId,
            Long itemNumber,
            ItemCostStatus status
    );
}