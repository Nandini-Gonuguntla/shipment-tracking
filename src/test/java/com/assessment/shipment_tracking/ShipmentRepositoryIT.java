package com.assessment.shipment_tracking;

import com.assessment.shipment_tracking.domain.Shipment;
import com.assessment.shipment_tracking.repository.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "RUN_TESTCONTAINERS", matches = "true")
class ShipmentRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("shipment_tracking")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Test
    void isolatesShipmentLookupByTenantInPostgres() {
        shipmentRepository.save(new Shipment("SHP-12345", "tenant-a", "Chicago", "Dallas", "FastFreight"));

        assertThat(shipmentRepository.findByShipmentIdAndTenantId("SHP-12345", "tenant-a")).isPresent();
        assertThat(shipmentRepository.findByShipmentIdAndTenantId("SHP-12345", "tenant-b")).isEmpty();
    }
}
