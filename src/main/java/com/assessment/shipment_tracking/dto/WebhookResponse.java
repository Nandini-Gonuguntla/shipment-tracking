package com.assessment.shipment_tracking.dto;

import java.time.Instant;
import java.util.UUID;

public record WebhookResponse(
        UUID webhookId,
        String targetUrl,
        String eventTypes,
        boolean active,
        Instant createdAt
) {
}
