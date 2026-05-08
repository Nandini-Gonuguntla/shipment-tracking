package com.assessment.shipment_tracking;

import com.assessment.shipment_tracking.repository.ShipmentEventRepository;
import com.assessment.shipment_tracking.repository.ShipmentRepository;
import com.assessment.shipment_tracking.repository.WebhookDeliveryLogRepository;
import com.assessment.shipment_tracking.repository.WebhookRepository;
import com.assessment.shipment_tracking.security.JwtService;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ShipmentApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentEventRepository eventRepository;

    @Autowired
    private WebhookRepository webhookRepository;

    @Autowired
    private WebhookDeliveryLogRepository deliveryLogRepository;

    @BeforeEach
    void cleanDatabase() {
        deliveryLogRepository.deleteAll();
        eventRepository.deleteAll();
        webhookRepository.deleteAll();
        shipmentRepository.deleteAll();
    }

    @Test
    void rejectsRequestsWithoutJwt() throws Exception {
        mockMvc.perform(get("/api/v1/shipments/SHP-12345/status"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void recordsEventAndReturnsCurrentStatus() throws Exception {
        createEvent("tenant-a", "SHP-12345", "IN_TRANSIT")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId", startsWith("EVT-")))
                .andExpect(jsonPath("$.shipmentId").value("SHP-12345"))
                .andExpect(jsonPath("$.eventType").value("IN_TRANSIT"));

        mockMvc.perform(get("/api/v1/shipments/SHP-12345/status")
                        .header(HttpHeaders.AUTHORIZATION, bearer("tenant-a")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentId").value("SHP-12345"))
                .andExpect(jsonPath("$.currentStatus").value("IN_TRANSIT"))
                .andExpect(jsonPath("$.carrier").value("FastFreight"));
    }

    @Test
    void returnsShipmentHistoryWithPagination() throws Exception {
        createEvent("tenant-a", "SHP-12345", "PICKUP").andExpect(status().isCreated());
        createEvent("tenant-a", "SHP-12345", "IN_TRANSIT").andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/shipments/SHP-12345/events?page=0&size=1")
                        .header(HttpHeaders.AUTHORIZATION, bearer("tenant-a")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void enforcesTenantIsolation() throws Exception {
        createEvent("tenant-a", "SHP-12345", "IN_TRANSIT").andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/shipments/SHP-12345/status")
                        .header(HttpHeaders.AUTHORIZATION, bearer("tenant-b")))
                .andExpect(status().isNotFound());
    }

    @Test
    void validatesInvalidLocationPayloads() throws Exception {
        String body = """
                {
                  "eventType": "IN_TRANSIT",
                  "timestamp": "2026-04-17T14:30:00Z",
                  "location": {"latitude": 140.0, "longitude": -74.0060, "address": "New York, NY"},
                  "metadata": {"carrier": "FastFreight"}
                }
                """;

        mockMvc.perform(post("/api/v1/shipments/SHP-12345/events")
                        .header(HttpHeaders.AUTHORIZATION, bearer("tenant-a"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors['location.latitude']").exists());
    }

    @Test
    void registersAndUnregistersWebhookSubscriptions() throws Exception {
        String response = mockMvc.perform(post("/api/v1/webhooks")
                        .header(HttpHeaders.AUTHORIZATION, bearer("tenant-a"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "targetUrl": "https://example.com/webhooks/shipments",
                                  "secret": "super-secret-value",
                                  "eventTypes": ["IN_TRANSIT", "DELIVERED"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String webhookId = response.replaceAll(".*\\\"webhookId\\\":\\\"([^\\\"]+)\\\".*", "$1");

        createEvent("tenant-a", "SHP-12345", "IN_TRANSIT").andExpect(status().isCreated());
        org.assertj.core.api.Assertions.assertThat(deliveryLogRepository.count()).isEqualTo(1);

        mockMvc.perform(delete("/api/v1/webhooks/" + webhookId)
                        .header(HttpHeaders.AUTHORIZATION, bearer("tenant-a")))
                .andExpect(status().isNoContent());
    }

    private org.springframework.test.web.servlet.ResultActions createEvent(String tenantId, String shipmentId, String eventType)
            throws Exception {
        String body = """
                {
                  "eventType": "%s",
                  "timestamp": "2026-04-17T14:30:00Z",
                  "location": {"latitude": 40.7128, "longitude": -74.0060, "address": "New York, NY"},
                  "metadata": {"carrier": "FastFreight", "vehicle": "TRUCK-789"}
                }
                """.formatted(eventType);
        return mockMvc.perform(post("/api/v1/shipments/" + shipmentId + "/events")
                .header(HttpHeaders.AUTHORIZATION, bearer(tenantId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private String bearer(String tenantId) {
        return "Bearer " + jwtService.createToken("api-client", tenantId, Instant.now().plusSeconds(3600));
    }
}
