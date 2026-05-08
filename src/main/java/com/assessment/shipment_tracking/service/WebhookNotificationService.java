package com.assessment.shipment_tracking.service;

import com.assessment.shipment_tracking.domain.ShipmentEvent;
import com.assessment.shipment_tracking.domain.Webhook;
import com.assessment.shipment_tracking.domain.WebhookDeliveryLog;
import com.assessment.shipment_tracking.repository.WebhookDeliveryLogRepository;
import com.assessment.shipment_tracking.repository.WebhookRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WebhookNotificationService {
    private final WebhookRepository webhookRepository;
    private final WebhookDeliveryLogRepository deliveryLogRepository;

    public WebhookNotificationService(WebhookRepository webhookRepository,
                                      WebhookDeliveryLogRepository deliveryLogRepository) {
        this.webhookRepository = webhookRepository;
        this.deliveryLogRepository = deliveryLogRepository;
    }

    public void notifySubscribers(ShipmentEvent event) {
        List<Webhook> webhooks = webhookRepository.findByTenantIdAndActiveTrue(event.getTenantId());
        for (Webhook webhook : webhooks) {
            if (webhook.getEventTypes().contains(event.getEventType().name())) {
                deliveryLogRepository.save(new WebhookDeliveryLog(
                        event.getTenantId(),
                        webhook.getWebhookId(),
                        event.getEventId(),
                        202,
                        "QUEUED"
                ));
            }
        }
    }
}
