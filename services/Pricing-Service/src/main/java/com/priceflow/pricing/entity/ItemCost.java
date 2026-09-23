package com.priceflow.pricing.entity;

import com.priceflow.pricing.enums.ItemCostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("item_costs")
public class ItemCost {

    @Id
    private Long id;

    @Column("request_id")
    private String requestId;

    @Column("vendor_id")
    private String vendorId;

    @Column("item_number")
    private Long itemNumber;

    @Column("cost")
    private BigDecimal cost;

    @Column("effective_date")
    private LocalDate effectiveDate;

    @Column("status")
    private ItemCostStatus status;

    @Column("event_id")
    private String eventId;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}