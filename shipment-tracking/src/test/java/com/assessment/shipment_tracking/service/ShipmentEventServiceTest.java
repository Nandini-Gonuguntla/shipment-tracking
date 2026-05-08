package com.assessment.shipment_tracking.service;

import com.assessment.shipment_tracking.domain.EventType;
import com.assessment.shipment_tracking.domain.ShipmentEvent;
import com.assessment.shipment_tracking.dto.LocationRequest;
import com.assessment.shipment_tracking.dto.ShipmentEventRequest;
import com.assessment.shipment_tracking.repository.ShipmentEventRepository;
import com.assessment.shipment_tracking.repository.ShipmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentEventServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private ShipmentEventRepository eventRepository;

    @Mock
    private WebhookNotificationService webhookNotificationService;

    @Test
    void recordEventCreatesShipmentWhenFirstEventArrives() {
        ShipmentEventService service = new ShipmentEventService(
                shipmentRepository,
                eventRepository,
                new JsonStringMapper(new ObjectMapper()),
                webhookNotificationService
        );
        ShipmentEventRequest request = new ShipmentEventRequest(
                EventType.IN_TRANSIT,
                Instant.parse("2026-04-17T14:30:00Z"),
                new LocationRequest(40.7128, -74.0060, "New York, NY"),
                Map.of("carrier", "FastFreight"),
                null,
                null,
                null,
                null,
                "GOOD"
        );
        when(shipmentRepository.findByShipmentIdAndTenantId("SHP-12345", "tenant-a")).thenReturn(Optional.empty());
        when(eventRepository.save(any(ShipmentEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.recordEvent("tenant-a", "SHP-12345", request);

        assertThat(response.shipmentId()).isEqualTo("SHP-12345");
        assertThat(response.eventType()).isEqualTo(EventType.IN_TRANSIT);
        ArgumentCaptor<ShipmentEvent> eventCaptor = ArgumentCaptor.forClass(ShipmentEvent.class);
        verify(eventRepository).save(eventCaptor.capture());
        verify(webhookNotificationService).notifySubscribers(eventCaptor.getValue());
        assertThat(eventCaptor.getValue().getTenantId()).isEqualTo("tenant-a");
    }
}
