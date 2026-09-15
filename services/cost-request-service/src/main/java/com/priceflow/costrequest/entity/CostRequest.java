package com.priceflow.costrequest.entity;

import com.priceflow.costrequest.enums.CostRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Version;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("cost_requests")
public class CostRequest {

    @Id
    @Column("request_id")
    private String requestId;

    @Version
    private Long version;

    @Column("vendor_id")
    private String vendorId;

    @Column("item_number")
    private Long itemNumber;

    @Column("proposed_cost")
    private BigDecimal proposedCost;

    @Column("reason")
    private String reason;

    @Column("status")
    private CostRequestStatus status;

    @Column("effective_date")
    private LocalDate effectiveDate;

    @Column("approved_by")
    private String approvedBy;

    @Column("approved_at")
    private Instant approvedAt;

    @Column("rejected_by")
    private String rejectedBy;

    @Column("rejected_at")
    private Instant rejectedAt;

    @Column("rejection_reason")
    private String rejectionReason;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}