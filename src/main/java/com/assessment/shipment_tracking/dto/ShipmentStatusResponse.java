package com.assessment.shipment_tracking.dto;

import java.time.Instant;

public record ShipmentStatusResponse(
        String shipmentId,
        String currentStatus,
        String latestLocation,
        Instant estimatedDeliveryAt,
        String condition,
        String carrier,
        Instant updatedAt
) {
}
