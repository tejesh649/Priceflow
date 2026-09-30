package com.priceflow.pricing.consumer;

import com.priceflow.events.costrequest.CostRequestApprovedEvent;
import com.priceflow.pricing.service.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CostRequestApprovedEventConsumer {

    private final PricingService pricingService;

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

        pricingService.processApprovedCost(event).block();
    }
}