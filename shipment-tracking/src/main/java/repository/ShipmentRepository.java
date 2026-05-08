package com.assessment.shipment_tracking.repository;

import com.assessment.shipment_tracking.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
    List<Shipment> findByCurrentStatus(String currentStatus);

}
