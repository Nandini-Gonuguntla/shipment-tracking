package com.assessment.shipment_tracking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "shipment_events", indexes = {
        @Index(name = "idx_events_tenant_shipment_time", columnList = "tenantId,shipmentId,timestamp"),
        @Index(name = "idx_events_tenant_type_time", columnList = "tenantId,eventType,timestamp")
})
public class ShipmentEvent {

    @Id
    @Column(length = 80, nullable = false)
    private String eventId;

    @Column(nullable = false, length = 80)
    private String tenantId;

    @Column(nullable = false, length = 80)
    private String shipmentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EventType eventType;

    @Column(nullable = false)
    private Instant timestamp;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private String location;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private String metadata;

    private Instant estimatedDeliveryAt;

    @Column(length = 80)
    private String condition;

    @ManyToOne(optional = false)
    private Shipment shipment;

    protected ShipmentEvent() {
    }

    public ShipmentEvent(String tenantId, String shipmentId, EventType eventType, Instant timestamp,
                         String location, String metadata, Instant estimatedDeliveryAt, String condition,
                         Shipment shipment) {
        this.eventId = "EVT-" + UUID.randomUUID();
        this.tenantId = tenantId;
        this.shipmentId = shipmentId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.location = location;
        this.metadata = metadata;
        this.estimatedDeliveryAt = estimatedDeliveryAt;
        this.condition = condition;
        this.shipment = shipment;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getLocation() {
        return location;
    }

    public String getMetadata() {
        return metadata;
    }

    public Instant getEstimatedDeliveryAt() {
        return estimatedDeliveryAt;
    }

    public String getCondition() {
        return condition;
    }
}
