package com.assessment.shipment_tracking.dto;

import com.assessment.shipment_tracking.domain.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record WebhookRequest(
        @NotBlank String targetUrl,
        @NotBlank @Size(min = 16, max = 200) String secret,
        @NotEmpty Set<EventType> eventTypes
) {
}
