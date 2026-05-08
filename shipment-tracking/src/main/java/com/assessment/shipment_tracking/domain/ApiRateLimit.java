package com.assessment.shipment_tracking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "api_rate_limits", indexes = @Index(name = "idx_rate_limits_tenant_window", columnList = "tenantId,windowStart"))
public class ApiRateLimit {

    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String tenantId;

    @Column(nullable = false)
    private Instant windowStart;

    @Column(nullable = false)
    private int requestCount;

    protected ApiRateLimit() {
    }

    public ApiRateLimit(String tenantId, Instant windowStart, int requestCount) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.windowStart = windowStart;
        this.requestCount = requestCount;
    }
}
