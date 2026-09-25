package com.priceflow.notification_service.controller;

import com.priceflow.notification_service.service.DltReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dlt")
@RequiredArgsConstructor
public class DltReplayController {

    private final DltReplayService dltReplayService;

    @PostMapping("/replay")
    public ResponseEntity<Map<String, Object>> replayAll() {

        int replayedCount = dltReplayService.replayAll();

        return ResponseEntity.ok(
                Map.of(
                        "status", "SUCCESS",
                        "replayedCount", replayedCount
                )
        );
    }
}