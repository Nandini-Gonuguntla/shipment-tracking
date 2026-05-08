package com.assessment.shipment_tracking.service;

import com.assessment.shipment_tracking.domain.Shipment;
import com.assessment.shipment_tracking.domain.ShipmentEvent;
import com.assessment.shipment_tracking.dto.ShipmentEventRequest;
import com.assessment.shipment_tracking.dto.ShipmentEventResponse;
import com.assessment.shipment_tracking.dto.ShipmentStatusResponse;
import com.assessment.shipment_tracking.exception.NotFoundException;
import com.assessment.shipment_tracking.repository.ShipmentEventRepository;
import com.assessment.shipment_tracking.repository.ShipmentRepository;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShipmentEventService {
    private final ShipmentRepository shipmentRepository;
    private final ShipmentEventRepository eventRepository;
    private final JsonStringMapper jsonStringMapper;
    private final WebhookNotificationService webhookNotificationService;

    public ShipmentEventService(ShipmentRepository shipmentRepository,
                                ShipmentEventRepository eventRepository,
                                JsonStringMapper jsonStringMapper,
                                WebhookNotificationService webhookNotificationService) {
        this.shipmentRepository = shipmentRepository;
        this.eventRepository = eventRepository;
        this.jsonStringMapper = jsonStringMapper;
        this.webhookNotificationService = webhookNotificationService;
    }

    @Transactional
    public ShipmentEventResponse recordEvent(String tenantId, String shipmentId, ShipmentEventRequest request) {
        Shipment shipment = shipmentRepository.findByShipmentIdAndTenantId(shipmentId, tenantId)
                .orElseGet(() -> new Shipment(
                        shipmentId,
                        tenantId,
                        defaultValue(request.origin(), "UNKNOWN"),
                        defaultValue(request.destination(), "UNKNOWN"),
                        defaultValue(request.carrier(), carrierFromMetadata(request))
                ));

        ShipmentEvent event = new ShipmentEvent(
                tenantId,
                shipmentId,
                request.eventType(),
                request.timestamp(),
                jsonStringMapper.toJson(Map.of(
                        "latitude", request.location().latitude(),
                        "longitude", request.location().longitude(),
                        "address", request.location().address()
                )),
                jsonStringMapper.toJson(request.metadata()),
                request.estimatedDeliveryAt(),
                request.condition() == null ? "UNKNOWN" : request.condition(),
                shipment
        );
        shipment.applyEvent(event);
        shipmentRepository.save(shipment);
        ShipmentEvent saved = eventRepository.save(event);
        webhookNotificationService.notifySubscribers(saved);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ShipmentEventResponse> getEvents(String tenantId, String shipmentId, Pageable pageable) {
        ensureShipmentExists(tenantId, shipmentId);
        return eventRepository.findByShipmentIdAndTenantIdOrderByTimestampDesc(shipmentId, tenantId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ShipmentStatusResponse getStatus(String tenantId, String shipmentId) {
        Shipment shipment = ensureShipmentExists(tenantId, shipmentId);
        return new ShipmentStatusResponse(
                shipment.getShipmentId(),
                shipment.getCurrentStatus(),
                shipment.getLatestLocation(),
                shipment.getEstimatedDeliveryAt(),
                shipment.getCondition(),
                shipment.getCarrier(),
                shipment.getUpdatedAt()
        );
    }

    private Shipment ensureShipmentExists(String tenantId, String shipmentId) {
        return shipmentRepository.findByShipmentIdAndTenantId(shipmentId, tenantId)
                .orElseThrow(() -> new NotFoundException("Shipment not found"));
    }

    private ShipmentEventResponse toResponse(ShipmentEvent event) {
        return new ShipmentEventResponse(
                event.getEventId(),
                event.getShipmentId(),
                event.getEventType(),
                event.getTimestamp(),
                event.getLocation(),
                event.getMetadata()
        );
    }

    private String carrierFromMetadata(ShipmentEventRequest request) {
        if (request.metadata() == null) {
            return "UNKNOWN";
        }
        return defaultValue(request.metadata().get("carrier"), "UNKNOWN");
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
