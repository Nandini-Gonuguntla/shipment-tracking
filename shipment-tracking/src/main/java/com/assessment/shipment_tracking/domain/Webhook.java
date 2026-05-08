package com.assessment.shipment_tracking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "webhooks", indexes = @Index(name = "idx_webhooks_tenant_active", columnList = "tenantId,active"))
public class Webhook {

    @Id
    private UUID webhookId;

    @Column(nullable = false, length = 80)
    private String tenantId;

    @Column(nullable = false, length = 500)
    private String targetUrl;

    @Column(nullable = false, length = 200)
    private String secret;

    @Column(nullable = false, length = 500)
    private String eventTypes;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private Instant createdAt;

    protected Webhook() {
    }

    public Webhook(String tenantId, String targetUrl, String secret, String eventTypes) {
        this.webhookId = UUID.randomUUID();
        this.tenantId = tenantId;
        this.targetUrl = targetUrl;
        this.secret = secret;
        this.eventTypes = eventTypes;
        this.active = true;
        this.createdAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getWebhookId() {
        return webhookId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public String getSecret() {
        return secret;
    }

    public String getEventTypes() {
        return eventTypes;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
