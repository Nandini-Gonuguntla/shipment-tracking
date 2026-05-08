package com.assessment.shipment_tracking.repository;

import com.assessment.shipment_tracking.domain.WebhookDeliveryLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookDeliveryLogRepository extends JpaRepository<WebhookDeliveryLog, UUID> {
}
