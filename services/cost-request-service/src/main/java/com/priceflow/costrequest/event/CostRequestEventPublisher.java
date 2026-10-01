package com.priceflow.costrequest.event;

import com.priceflow.events.costrequest.CostRequestApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.priceflow.events.costrequest.CostRequestRejectedEvent;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CostRequestEventPublisher {

    private static final String TOPIC =
            "priceflow.cost-request.events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Mono<Void> publishApproved(
            CostRequestApprovedEvent event) {

        log.info(
                "Publishing COST_REQUEST_APPROVED event requestId={}, eventId={}",
                event.getRequestId(),
                event.getEventId()
        );

        return Mono.fromFuture(
                        kafkaTemplate.send(
                                TOPIC,
                                event.getRequestId().toString(),
                                event
                        )
                )
                .doOnSuccess(result ->
                        log.info(
                                "Published COST_REQUEST_APPROVED event requestId={}, partition={}, offset={}",
                                event.getRequestId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        )
                )
                .doOnError(error ->
                        log.error(
                                "Failed to publish COST_REQUEST_APPROVED event requestId={}",
                                event.getRequestId(),
                                error
                        )
                )
                .then();
    }

    public Mono<Void> publishRejected(
            CostRequestRejectedEvent event) {

        log.info(
                "Publishing COST_REQUEST_REJECTED event requestId={}, eventId={}",
                event.getRequestId(),
                event.getEventId()
        );

        return Mono.fromFuture(
                        kafkaTemplate.send(
                                TOPIC,
                                event.getRequestId().toString(),
                                event
                        )
                )
                .doOnSuccess(result ->
                        log.info(
                                "Published COST_REQUEST_REJECTED event requestId={}, partition={}, offset={}",
                                event.getRequestId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        )
                )
                .doOnError(error ->
                        log.error(
                                "Failed to publish COST_REQUEST_REJECTED event requestId={}",
                                event.getRequestId(),
                                error
                        )
                )
                .then();
    }
}