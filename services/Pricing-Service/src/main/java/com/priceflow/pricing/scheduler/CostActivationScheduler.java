package com.priceflow.pricing.scheduler;

import com.priceflow.pricing.service.CostActivationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CostActivationScheduler {

    private final CostActivationService costActivationService;

    @Scheduled(
            cron = "${priceflow.pricing.activation.cron}",
            zone = "${priceflow.pricing.activation.zone}"
    )
    public void activateScheduledCosts() {

        log.info("Starting daily scheduled cost activation");

        costActivationService.activateEligibleCosts()
                .doOnComplete(() ->
                        log.info("Daily scheduled cost activation completed")
                )
                .doOnError(error ->
                        log.error(
                                "Daily scheduled cost activation failed",
                                error
                        )
                )
                .subscribe();
    }
}