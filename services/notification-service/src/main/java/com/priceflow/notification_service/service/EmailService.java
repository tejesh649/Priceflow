package com.priceflow.notification_service.service;

import com.priceflow.events.costrequest.CostRequestApprovedEvent;
import com.priceflow.events.costrequest.CostRequestRejectedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${priceflow.notification.test-recipient}")
    private String testRecipient;

    public void sendTestEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(testRecipient);
        message.setSubject("Priceflow Notification Service Test");
        message.setText(
                "Priceflow notification_service is configured successfully."
        );

        mailSender.send(message);
        log.info("Test email sent successfully to {}", testRecipient);
    }

    public void sendApprovalTestEmail() throws MessagingException {

        Context context = new Context();

        context.setVariable("requestId", "CR-TEST-1001");
        context.setVariable("vendorId", "V20003");
        context.setVariable("itemNumber", 654321L);
        context.setVariable("approvedCost", new BigDecimal("15.75"));
        context.setVariable("effectiveDate", LocalDate.now().plusDays(1));
        context.setVariable("approvedBy", "merchant-user");
        context.setVariable("approvedAt", LocalDateTime.now());

        String htmlContent =
                templateEngine.process("cost-request-approved", context);

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(testRecipient);
        helper.setSubject("Priceflow - Cost Request Approved");
        helper.setText(htmlContent, true);

        mailSender.send(message);

        log.info(
                "Approval test email sent successfully to {}",
                testRecipient
        );
    }

    public void sendCostRequestApprovedEmail(
            CostRequestApprovedEvent event) throws MessagingException {

        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern("MMMM d, yyyy");

        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a z");

        ZoneId easternZone = ZoneId.of("America/New_York");

        String formattedCost =
                NumberFormat.getCurrencyInstance(Locale.US)
                        .format(event.getApprovedCost());

        String formattedEffectiveDate =
                event.getEffectiveDate().format(dateFormatter);

        String formattedApprovedAt =
                event.getApprovedAt()
                        .atZone(easternZone)
                        .format(dateTimeFormatter);

        Context context = new Context();

        context.setVariable("requestId", event.getRequestId());
        context.setVariable("vendorId", event.getVendorId());
        context.setVariable("itemNumber", event.getItemNumber());
        context.setVariable("approvedCost", formattedCost);
        context.setVariable("effectiveDate", formattedEffectiveDate);
        context.setVariable("approvedBy", event.getApprovedBy());
        context.setVariable("approvedAt", formattedApprovedAt);

        String htmlContent =
                templateEngine.process("cost-request-approved", context);

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(testRecipient);
        helper.setSubject(
                "Priceflow - Cost Request " +
                        event.getRequestId() +
                        " Approved"
        );
        helper.setText(htmlContent, true);

        mailSender.send(message);

        log.info(
                "Cost request approval email sent successfully. " +
                        "requestId={}, recipient={}",
                event.getRequestId(),
                testRecipient
        );
    }

    public void sendCostRequestRejectedEmail(
            CostRequestRejectedEvent event) throws MessagingException {

        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a z");

        ZoneId easternZone = ZoneId.of("America/New_York");

        String formattedCost =
                NumberFormat.getCurrencyInstance(Locale.US)
                        .format(event.getProposedCost());

        String formattedRejectedAt =
                event.getRejectedAt()
                        .atZone(easternZone)
                        .format(dateTimeFormatter);

        Context context = new Context();

        context.setVariable("requestId", event.getRequestId());
        context.setVariable("vendorId", event.getVendorId());
        context.setVariable("itemNumber", event.getItemNumber());
        context.setVariable("proposedCost", formattedCost);
        context.setVariable("rejectedBy", event.getRejectedBy());
        context.setVariable("rejectedAt", formattedRejectedAt);
        context.setVariable(
                "rejectionReason",
                event.getRejectionReason()
        );

        String htmlContent =
                templateEngine.process(
                        "cost-request-rejected",
                        context
                );

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(testRecipient);
        helper.setSubject(
                "Priceflow - Cost Request " +
                        event.getRequestId() +
                        " Rejected"
        );
        helper.setText(htmlContent, true);

        mailSender.send(message);

        log.info(
                "Cost request rejection email sent successfully. " +
                        "requestId={}, recipient={}",
                event.getRequestId(),
                testRecipient
        );
    }
}