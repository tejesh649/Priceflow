package com.priceflow.notification_service.consumer;

import com.priceflow.events.costrequest.CostRequestApprovedEvent;
import com.priceflow.events.costrequest.CostRequestRejectedEvent;
import com.priceflow.notification_service.service.EmailService;
import com.priceflow.notification_service.service.NotificationDeliveryService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CostRequestEventConsumer {

    private final EmailService emailService;

    private final NotificationDeliveryService notificationDeliveryService;

    @KafkaListener(
            topics = "priceflow.cost-request.events",
            groupId = "notification-service"
    )
    public void consume(SpecificRecord event)
            throws MessagingException {

        if (event instanceof CostRequestApprovedEvent approvedEvent) {

            String eventId = approvedEvent.getEventId().toString();

            boolean alreadySent = Boolean.TRUE.equals(
                    notificationDeliveryService
                            .isAlreadySent(eventId)
                            .block()
            );

            if (alreadySent) {
                log.info(
                        "Skipping duplicate COST_REQUEST_APPROVED event. eventId={}, requestId={}",
                        eventId,
                        approvedEvent.getRequestId()
                );
                return;
            }

            log.info(
                    "Received COST_REQUEST_APPROVED event: requestId={}, vendorId={}, itemNumber={}",
                    approvedEvent.getRequestId(),
                    approvedEvent.getVendorId(),
                    approvedEvent.getItemNumber()
            );

            String recipient =
                    emailService.sendCostRequestApprovedEmail(approvedEvent);

            notificationDeliveryService
                    .markAsSent(
                            eventId,
                            approvedEvent.getEventType().toString(),
                            approvedEvent.getRequestId().toString(),
                            recipient
                    )
                    .block();
        } else if (event instanceof CostRequestRejectedEvent rejectedEvent) {

            log.info(
                    "Received COST_REQUEST_REJECTED event: requestId={}, vendorId={}, itemNumber={}",
                    rejectedEvent.getRequestId(),
                    rejectedEvent.getVendorId(),
                    rejectedEvent.getItemNumber()
            );

            String eventId = rejectedEvent.getEventId().toString();

            boolean alreadySent = Boolean.TRUE.equals(
                    notificationDeliveryService
                            .isAlreadySent(eventId)
                            .block()
            );

            if (alreadySent) {
                log.info(
                        "Skipping duplicate COST_REQUEST_REJECTED event. eventId={}, requestId={}",
                        eventId,
                        rejectedEvent.getRequestId()
                );
                return;
            }

            String recipient =
                    emailService.sendCostRequestRejectedEmail(rejectedEvent);

            notificationDeliveryService
                    .markAsSent(
                            eventId,
                            rejectedEvent.getEventType().toString(),
                            rejectedEvent.getRequestId().toString(),
                            recipient
                    )
                    .block();
        }
    }
}