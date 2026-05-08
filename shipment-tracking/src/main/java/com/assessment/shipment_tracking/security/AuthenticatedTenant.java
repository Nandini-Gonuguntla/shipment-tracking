package com.assessment.shipment_tracking.security;

import java.security.Principal;

public record AuthenticatedTenant(String tenantId, String subject) implements Principal {
    @Override
    public String getName() {
        return subject;
    }
}
