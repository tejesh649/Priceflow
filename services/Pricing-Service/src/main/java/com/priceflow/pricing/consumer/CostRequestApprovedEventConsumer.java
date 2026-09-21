package com.priceflow.pricing.consumer;

import com.priceflow.events.costrequest.CostRequestApprovedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CostRequestApprovedEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(CostRequestApprovedEventConsumer.class);

    @KafkaListener(
            topics = "priceflow.cost-request.events",
            groupId = "pricing-service"
    )
    public void consume(CostRequestApprovedEvent event) {

        log.info(
                "Received COST_REQUEST_APPROVED event: requestId={}, vendorId={}, itemNumber={}, approvedCost={}, effectiveDate={}",
                event.getRequestId(),
                event.getVendorId(),
                event.getItemNumber(),
                event.getApprovedCost(),
                event.getEffectiveDate()
        );
    }
}