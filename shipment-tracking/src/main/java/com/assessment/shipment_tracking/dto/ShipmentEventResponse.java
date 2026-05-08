package com.assessment.shipment_tracking.dto;

import com.assessment.shipment_tracking.domain.EventType;
import java.time.Instant;

public record ShipmentEventResponse(
        String eventId,
        String shipmentId,
        EventType eventType,
        Instant timestamp,
        String location,
        String metadata
) {
}
