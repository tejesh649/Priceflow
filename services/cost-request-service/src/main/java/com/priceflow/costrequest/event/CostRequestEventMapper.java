package com.priceflow.costrequest.event;

import com.priceflow.costrequest.entity.CostRequest;
import com.priceflow.events.costrequest.CostRequestApprovedEvent;
import org.springframework.stereotype.Component;

import org.apache.avro.Conversions;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

import java.time.Instant;
import java.util.UUID;

@Component
public class CostRequestEventMapper {

    public CostRequestApprovedEvent toApprovedEvent(
            CostRequest costRequest) {

        return CostRequestApprovedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("COST_REQUEST_APPROVED")
                .setEventVersion(1)
                .setOccurredAt(Instant.now())
                .setRequestId(costRequest.getRequestId())
                .setVendorId(costRequest.getVendorId())
                .setItemNumber(costRequest.getItemNumber())
                .setApprovedCost(
                        toAvroDecimal(costRequest.getProposedCost())
                )                .setEffectiveDate(costRequest.getEffectiveDate())
                .setApprovedBy(costRequest.getApprovedBy())
                .setApprovedAt(costRequest.getApprovedAt())
                .build();
    }

    private ByteBuffer toAvroDecimal(BigDecimal value) {

        Schema decimalSchema = CostRequestApprovedEvent
                .getClassSchema()
                .getField("approvedCost")
                .schema();

        LogicalTypes.Decimal decimalType =
                (LogicalTypes.Decimal) decimalSchema.getLogicalType();

        return new Conversions.DecimalConversion()
                .toBytes(
                        value,
                        decimalSchema,
                        decimalType
                );
    }
}