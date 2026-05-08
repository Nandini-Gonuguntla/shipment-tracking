package com.assessment.shipment_tracking.controller;

import com.assessment.shipment_tracking.security.JwtService;
import java.time.Instant;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("local")
public class LocalTokenController {
    private final JwtService jwtService;

    public LocalTokenController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/local/token")
    public Map<String, String> token(@RequestParam(defaultValue = "tenant-a") String tenantId) {
        String token = jwtService.createToken("local-demo-client", tenantId, Instant.now().plusSeconds(3600));
        return Map.of("token", token, "tenantId", tenantId);
    }
}
