package com.assessment.shipment_tracking.controller;

import com.assessment.shipment_tracking.dto.ShipmentEventRequest;
import com.assessment.shipment_tracking.dto.ShipmentEventResponse;
import com.assessment.shipment_tracking.dto.ShipmentStatusResponse;
import com.assessment.shipment_tracking.dto.PagedResponse;
import com.assessment.shipment_tracking.security.CurrentTenant;
import com.assessment.shipment_tracking.service.ShipmentEventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shipments/{shipmentId}")
public class ShipmentEventController {
    private final ShipmentEventService shipmentEventService;
    private final CurrentTenant currentTenant;

    public ShipmentEventController(ShipmentEventService shipmentEventService, CurrentTenant currentTenant) {
        this.shipmentEventService = shipmentEventService;
        this.currentTenant = currentTenant;
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public ShipmentEventResponse recordEvent(@PathVariable String shipmentId,
                                             @Valid @RequestBody ShipmentEventRequest request) {
        return shipmentEventService.recordEvent(currentTenant.id(), shipmentId, request);
    }

    @GetMapping("/events")
    public PagedResponse<ShipmentEventResponse> getEvents(@PathVariable String shipmentId,
                                                          @PageableDefault(size = 50) Pageable pageable) {
        return PagedResponse.from(shipmentEventService.getEvents(currentTenant.id(), shipmentId, pageable));
    }

    @GetMapping("/status")
    public ShipmentStatusResponse getStatus(@PathVariable String shipmentId) {
        return shipmentEventService.getStatus(currentTenant.id(), shipmentId);
    }
}
