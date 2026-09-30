package com.priceflow.pricing.controller;

import com.priceflow.pricing.entity.ItemCost;
import com.priceflow.pricing.service.CostActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
public class CostActivationController {

    private final CostActivationService costActivationService;

    // Temporary endpoint only for testing can remove later.

    @PostMapping("/activate")
    public Flux<ItemCost> activateEligibleCosts() {
        return costActivationService.activateEligibleCosts();
    }
}