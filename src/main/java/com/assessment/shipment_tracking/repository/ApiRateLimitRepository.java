package com.assessment.shipment_tracking.repository;

import com.assessment.shipment_tracking.domain.ApiRateLimit;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiRateLimitRepository extends JpaRepository<ApiRateLimit, UUID> {
}
