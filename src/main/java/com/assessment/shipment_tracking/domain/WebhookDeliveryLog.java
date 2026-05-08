package com.assessment.shipment_tracking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "webhook_delivery_logs", indexes = @Index(name = "idx_delivery_logs_tenant_webhook", columnList = "tenantId,webhookId"))
public class WebhookDeliveryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 80)
    private String tenantId;

    @Column(nullable = false)
    private UUID webhookId;

    @Column(nullable = false, length = 80)
    private String eventId;

    @Column(nullable = false)
    private int statusCode;

    @Column(nullable = false, length = 40)
    private String deliveryStatus;

    @Column(nullable = false)
    private Instant attemptedAt;

    protected WebhookDeliveryLog() {
    }

    public WebhookDeliveryLog(String tenantId, UUID webhookId, String eventId, int statusCode, String deliveryStatus) {
        this.tenantId = tenantId;
        this.webhookId = webhookId;
        this.eventId = eventId;
        this.statusCode = statusCode;
        this.deliveryStatus = deliveryStatus;
        this.attemptedAt = Instant.now();
    }
}
