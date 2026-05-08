package com.assessment.shipment_tracking.repository;

import com.assessment.shipment_tracking.domain.Webhook;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookRepository extends JpaRepository<Webhook, UUID> {
    List<Webhook> findByTenantIdAndActiveTrue(String tenantId);

    Optional<Webhook> findByWebhookIdAndTenantId(UUID webhookId, String tenantId);
}
