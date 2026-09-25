package com.priceflow.notification_service.controller;

import com.priceflow.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    //Test endpoints might delete later
    @PostMapping("/test-email")
    public ResponseEntity<String> sendTestEmail() {

        emailService.sendTestEmail();

        return ResponseEntity.ok("Test email sent successfully");
    }

    @PostMapping("/test-approval-email")
    public ResponseEntity<String> sendApprovalTestEmail()
            throws MessagingException {

        emailService.sendApprovalTestEmail();

        return ResponseEntity.ok(
                "Approval test email sent successfully"
        );
    }
}