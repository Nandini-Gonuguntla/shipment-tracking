# Shipment Tracking API

Real-time shipment event tracking API for the Senior API Developer take-home assessment.

## Requirements Covered

- Record shipment events: `POST /api/v1/shipments/{shipmentId}/events`
- Retrieve shipment event history with pagination
- Retrieve current shipment status
- Register and unregister webhooks
- JWT authentication with tenant isolation
- Per-tenant rate limiting
- PostgreSQL schema with event partitioning/indexing strategy
- Unit, MockMvc, security, tenant isolation, rate limit, and TestContainers integration tests

## Prerequisites

- Java 17+
- Maven wrapper dependencies downloaded, or Maven 3.8+
- PostgreSQL 12+
- Docker Desktop only for TestContainers integration tests

## Configuration

Environment variables:

```text
DATABASE_URL=jdbc:postgresql://localhost:5432/shipment_tracking
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
JWT_SECRET=change-me-change-me-change-me-change-me
RATE_LIMIT_REQUESTS_PER_MINUTE=1000
SERVER_PORT=8081
```

Create the database objects:

```bash
psql -U postgres -d shipment_tracking -f db/schema.sql
```

## Run

For an in-person demo without local PostgreSQL setup, run with the `local` Spring profile. This uses an in-memory H2 database and creates the schema at startup.

In IntelliJ:

```text
Run Configuration -> Active profiles: local
```

Or add this VM option:

```text
-Dspring.profiles.active=local
```

On this machine, Maven is available through the wrapper distribution cache:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-17'
$env:SPRING_PROFILES_ACTIVE='local'
& 'C:\Users\Gonug\.m2\wrapper\dists\apache-maven-3.9.15-bin\4rlcemksed9vjmkvgss0jpc4po\apache-maven-3.9.15\bin\mvn.cmd' spring-boot:run
```

If the wrapper works in your shell:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-17'
.\mvnw.cmd spring-boot:run
```

## Test

Unit and API tests:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-17'
& 'C:\Users\Gonug\.m2\wrapper\dists\apache-maven-3.9.15-bin\4rlcemksed9vjmkvgss0jpc4po\apache-maven-3.9.15\bin\mvn.cmd' clean test
```

Enable the TestContainers PostgreSQL integration test when Docker Desktop is running:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-17'
$env:RUN_TESTCONTAINERS='true'
& 'C:\Users\Gonug\.m2\wrapper\dists\apache-maven-3.9.15-bin\4rlcemksed9vjmkvgss0jpc4po\apache-maven-3.9.15\bin\mvn.cmd' clean test
```

Coverage report:

```text
target/site/jacoco/index.html
```

## Example Request

```bash
curl -X POST http://localhost:8081/api/v1/shipments/SHP-12345/events \
  -H "Authorization: Bearer <jwt>" \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "IN_TRANSIT",
    "timestamp": "2026-04-17T14:30:00Z",
    "location": {
      "latitude": 40.7128,
      "longitude": -74.0060,
      "address": "New York, NY"
    },
    "metadata": {
      "carrier": "FastFreight",
      "vehicle": "TRUCK-789"
    }
  }'
```
