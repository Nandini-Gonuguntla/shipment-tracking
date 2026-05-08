package com.assessment.shipment_tracking.controller;

import com.assessment.shipment_tracking.entity.Shipment;
import com.assessment.shipment_tracking.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Shipment createShipment(@Valid @RequestBody Shipment shipment) {
        return shipmentService.createShipment(shipment);
    }

    @GetMapping
    public List<Shipment> getAllShipments() {
        return shipmentService.getAllShipments();
    }

    @GetMapping("/{id}")
    public Shipment getShipmentById(@PathVariable UUID id) {
        return shipmentService.getShipmentById(id);
    }

    @PutMapping("/{id}")
    public Shipment updateShipment(@PathVariable UUID id, @RequestBody Shipment shipment) {
        return shipmentService.updateShipment(id, shipment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShipment(@PathVariable UUID id) {
        shipmentService.deleteShipment(id);
    }

    @GetMapping("/status/{status}")
    public List<Shipment> getShipmentsByStatus(@PathVariable String status) {
        return shipmentService.getShipmentsByStatus(status);
    }
}