package com.priceflow.costrequest.repository;

import com.priceflow.costrequest.entity.CostRequest;
import com.priceflow.costrequest.enums.CostRequestStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CostRequestRepository
        extends ReactiveCrudRepository<CostRequest, String> {

    Flux<CostRequest> findByStatus(CostRequestStatus status);

    @Query("""
            SELECT *
            FROM cost_requests
            WHERE status = :status
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<CostRequest> findByStatusPaged(
            CostRequestStatus status,
            int limit,
            long offset
    );

    @Query("""
            SELECT COUNT(*)
            FROM cost_requests
            WHERE status = :status
            """)
    Mono<Long> countByStatus(CostRequestStatus status);

    @Query("""
            SELECT *
            FROM cost_requests
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<CostRequest> findAllPaged(
            int limit,
            long offset
    );
}