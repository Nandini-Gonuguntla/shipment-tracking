package com.assessment.shipment_tracking.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationRequest(
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
        @NotBlank String address
) {
    public String compact() {
        return latitude + "," + longitude + " " + address;
    }

    public String json() {
        return "{\"latitude\":" + latitude + ",\"longitude\":" + longitude + ",\"address\":\"" + address + "\"}";
    }
}
