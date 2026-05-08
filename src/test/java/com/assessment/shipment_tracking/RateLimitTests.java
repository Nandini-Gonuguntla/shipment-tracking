package com.assessment.shipment_tracking;

import com.assessment.shipment_tracking.security.JwtService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "rate-limit.requests-per-minute=2")
@AutoConfigureMockMvc
@DirtiesContext
class RateLimitTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void returns429AfterTenantExceedsRateLimit() throws Exception {
        String authorization = "Bearer " + jwtService.createToken("api-client", "tenant-rate", Instant.now().plusSeconds(3600));

        mockMvc.perform(get("/api/v1/shipments/MISSING/status").header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/v1/shipments/MISSING/status").header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/v1/shipments/MISSING/status").header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isTooManyRequests());
    }
}
