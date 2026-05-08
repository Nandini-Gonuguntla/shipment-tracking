package com.assessment.shipment_tracking.repository;

import com.assessment.shipment_tracking.domain.Shipment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, String> {
    Optional<Shipment> findByShipmentIdAndTenantId(String shipmentId, String tenantId);
}
