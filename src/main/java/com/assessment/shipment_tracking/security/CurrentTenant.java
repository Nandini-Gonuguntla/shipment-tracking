package com.assessment.shipment_tracking.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentTenant {
    public String id() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedTenant tenant)) {
            throw new IllegalStateException("Authenticated tenant is required");
        }
        return tenant.tenantId();
    }
}
