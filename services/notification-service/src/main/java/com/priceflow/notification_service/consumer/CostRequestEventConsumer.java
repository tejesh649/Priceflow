package com.priceflow.notification_service.consumer;

import com.priceflow.events.costrequest.CostRequestApprovedEvent;
import com.priceflow.events.costrequest.CostRequestRejectedEvent;
import com.priceflow.notification_service.service.EmailService;
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

    @KafkaListener(
            topics = "priceflow.cost-request.events",
            groupId = "notification-service"
    )
    public void consume(SpecificRecord event)
            throws MessagingException {

        if (event instanceof CostRequestApprovedEvent approvedEvent) {

            log.info(
                    "Received COST_REQUEST_APPROVED event: " +
                            "requestId={}, vendorId={}, itemNumber={}",
                    approvedEvent.getRequestId(),
                    approvedEvent.getVendorId(),
                    approvedEvent.getItemNumber()
            );

            emailService.sendCostRequestApprovedEmail(approvedEvent);

        } else if (event instanceof CostRequestRejectedEvent rejectedEvent) {

            log.info(
                    "Received COST_REQUEST_REJECTED event: " +
                            "requestId={}, vendorId={}, itemNumber={}",
                    rejectedEvent.getRequestId(),
                    rejectedEvent.getVendorId(),
                    rejectedEvent.getItemNumber()
            );

            // We'll implement this method in EmailService next.
            emailService.sendCostRequestRejectedEmail(rejectedEvent);

        } else {

            log.warn(
                    "Received unsupported cost request event type={}",
                    event.getClass().getName()
            );
        }
    }
}