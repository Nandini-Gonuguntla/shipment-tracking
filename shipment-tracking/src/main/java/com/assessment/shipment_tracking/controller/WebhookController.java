package com.assessment.shipment_tracking.controller;

import com.assessment.shipment_tracking.dto.WebhookRequest;
import com.assessment.shipment_tracking.dto.WebhookResponse;
import com.assessment.shipment_tracking.security.CurrentTenant;
import com.assessment.shipment_tracking.service.WebhookService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks")
public class WebhookController {
    private final WebhookService webhookService;
    private final CurrentTenant currentTenant;

    public WebhookController(WebhookService webhookService, CurrentTenant currentTenant) {
        this.webhookService = webhookService;
        this.currentTenant = currentTenant;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WebhookResponse register(@Valid @RequestBody WebhookRequest request) {
        return webhookService.register(currentTenant.id(), request);
    }

    @DeleteMapping("/{webhookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregister(@PathVariable UUID webhookId) {
        webhookService.unregister(currentTenant.id(), webhookId);
    }
}
