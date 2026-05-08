package com.assessment.shipment_tracking.service;

import com.assessment.shipment_tracking.entity.Shipment;
import com.assessment.shipment_tracking.repository.ShipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public Shipment createShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public Shipment getShipmentById(UUID id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
    }
    public List<Shipment> getShipmentsByStatus(String status) {
        return shipmentRepository.findByCurrentStatus(status);
    }

    public Shipment updateShipment(UUID id, Shipment updatedShipment) {
        Shipment shipment = shipmentRepository.findById(id).orElse(null);

        if (shipment != null) {
            shipment.setTenantId(updatedShipment.getTenantId());
            shipment.setShipmentNumber(updatedShipment.getShipmentNumber());
            shipment.setOrigin(updatedShipment.getOrigin());
            shipment.setDestination(updatedShipment.getDestination());
            shipment.setCarrier(updatedShipment.getCarrier());
            shipment.setCurrentStatus(updatedShipment.getCurrentStatus());

            return shipmentRepository.save(shipment);
        }

        return null;
    }

    public void deleteShipment(UUID id) {
        shipmentRepository.deleteById(id);
    }
}