package com.assessment.shipment_tracking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;



    @NotNull
    private UUID tenantId;

    @NotBlank
    private String shipmentNumber;

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    @NotBlank
    private String carrier;

    @NotBlank
    private String currentStatus;

}