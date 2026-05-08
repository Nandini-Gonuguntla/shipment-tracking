package com.assessment.shipment_tracking.service;

import com.assessment.shipment_tracking.domain.Webhook;
import com.assessment.shipment_tracking.dto.WebhookRequest;
import com.assessment.shipment_tracking.dto.WebhookResponse;
import com.assessment.shipment_tracking.exception.NotFoundException;
import com.assessment.shipment_tracking.repository.WebhookRepository;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebhookService {
    private final WebhookRepository webhookRepository;

    public WebhookService(WebhookRepository webhookRepository) {
        this.webhookRepository = webhookRepository;
    }

    @Transactional
    public WebhookResponse register(String tenantId, WebhookRequest request) {
        String eventTypes = request.eventTypes().stream()
                .map(Enum::name)
                .sorted()
                .collect(Collectors.joining(","));
        Webhook webhook = webhookRepository.save(new Webhook(tenantId, request.targetUrl(), request.secret(), eventTypes));
        return toResponse(webhook);
    }

    @Transactional
    public void unregister(String tenantId, UUID webhookId) {
        Webhook webhook = webhookRepository.findByWebhookIdAndTenantId(webhookId, tenantId)
                .orElseThrow(() -> new NotFoundException("Webhook not found"));
        webhook.deactivate();
        webhookRepository.save(webhook);
    }

    private WebhookResponse toResponse(Webhook webhook) {
        return new WebhookResponse(
                webhook.getWebhookId(),
                webhook.getTargetUrl(),
                webhook.getEventTypes(),
                webhook.isActive(),
                webhook.getCreatedAt()
        );
    }
}
