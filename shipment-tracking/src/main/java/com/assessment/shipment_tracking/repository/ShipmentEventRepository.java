package com.assessment.shipment_tracking.repository;

import com.assessment.shipment_tracking.domain.ShipmentEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentEventRepository extends JpaRepository<ShipmentEvent, String> {
    Page<ShipmentEvent> findByShipmentIdAndTenantIdOrderByTimestampDesc(String shipmentId, String tenantId, Pageable pageable);
}
