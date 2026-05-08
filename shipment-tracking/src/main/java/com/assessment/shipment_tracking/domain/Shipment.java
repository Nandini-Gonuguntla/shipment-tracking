package com.assessment.shipment_tracking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;

@Entity
@Table(name = "shipments", indexes = {
        @Index(name = "idx_shipments_tenant_status", columnList = "tenantId,currentStatus"),
        @Index(name = "idx_shipments_tenant_carrier", columnList = "tenantId,carrier")
})
public class Shipment {

    @Id
    @Column(length = 80, nullable = false)
    private String shipmentId;

    @Column(nullable = false, length = 80)
    private String tenantId;

    @Column(nullable = false, length = 200)
    private String origin;

    @Column(nullable = false, length = 200)
    private String destination;

    @Column(nullable = false, length = 120)
    private String carrier;

    @Column(nullable = false, length = 40)
    private String currentStatus;

    @Column(length = 500)
    private String latestLocation;

    private Instant estimatedDeliveryAt;

    @Column(length = 80)
    private String condition;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private long version;

    protected Shipment() {
    }

    public Shipment(String shipmentId, String tenantId, String origin, String destination, String carrier) {
        this.shipmentId = shipmentId;
        this.tenantId = tenantId;
        this.origin = origin;
        this.destination = destination;
        this.carrier = carrier;
        this.currentStatus = EventType.PICKUP.name();
        this.condition = "UNKNOWN";
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void applyEvent(ShipmentEvent event) {
        this.currentStatus = event.getEventType().name();
        this.latestLocation = event.getLocation();
        this.estimatedDeliveryAt = event.getEstimatedDeliveryAt();
        this.condition = event.getCondition();
        this.updatedAt = Instant.now();
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getLatestLocation() {
        return latestLocation;
    }

    public Instant getEstimatedDeliveryAt() {
        return estimatedDeliveryAt;
    }

    public String getCondition() {
        return condition;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
