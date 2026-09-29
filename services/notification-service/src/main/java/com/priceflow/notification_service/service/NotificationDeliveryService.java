package com.priceflow.notification_service.service;

import com.priceflow.notification_service.model.NotificationDelivery;
import com.priceflow.notification_service.repository.NotificationDeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDeliveryService {

    private final NotificationDeliveryRepository repository;

    public Mono<Boolean> isAlreadySent(String eventId) {

        return repository.existsById(eventId)
                .doOnNext(exists -> {
                    if (exists) {
                        log.info(
                                "Notification already processed. eventId={}",
                                eventId
                        );
                    }
                });
    }

    public Mono<NotificationDelivery> markAsSent(
            String eventId,
            String eventType,
            String requestId,
            String recipient) {

        Instant now = Instant.now();

        NotificationDelivery delivery =
                NotificationDelivery.builder()
                        .eventId(eventId)
                        .eventType(eventType)
                        .requestId(requestId)
                        .recipient(recipient)
                        .status("SENT")
                        .sentAt(now)
                        .createdAt(now)
                        .build();

        return repository.save(delivery)
                .doOnSuccess(saved ->
                        log.info(
                                "Notification delivery recorded. eventId={}, requestId={}, status=SENT",
                                eventId,
                                requestId
                        )
                );
    }


}