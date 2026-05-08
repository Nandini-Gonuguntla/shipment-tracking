package com.assessment.shipment_tracking.dto;

import com.assessment.shipment_tracking.domain.EventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;

public record ShipmentEventRequest(
        @NotNull EventType eventType,
        @NotNull Instant timestamp,
        @NotNull @Valid LocationRequest location,
        Map<String, String> metadata,
        Instant estimatedDeliveryAt,
        String origin,
        String destination,
        String carrier,
        String condition
) {
}
