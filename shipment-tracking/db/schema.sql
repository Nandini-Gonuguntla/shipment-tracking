CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE shipments (
    shipment_id VARCHAR(80) PRIMARY KEY,
    tenant_id VARCHAR(80) NOT NULL,
    origin VARCHAR(200) NOT NULL,
    destination VARCHAR(200) NOT NULL,
    carrier VARCHAR(120) NOT NULL,
    current_status VARCHAR(40) NOT NULL,
    latest_location VARCHAR(500),
    estimated_delivery_at TIMESTAMPTZ,
    condition VARCHAR(80),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_shipments_tenant_status ON shipments (tenant_id, current_status);
CREATE INDEX idx_shipments_tenant_carrier ON shipments (tenant_id, carrier);

CREATE TABLE shipment_events (
    event_id VARCHAR(80) PRIMARY KEY,
    tenant_id VARCHAR(80) NOT NULL,
    shipment_id VARCHAR(80) NOT NULL REFERENCES shipments (shipment_id),
    event_type VARCHAR(40) NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    location JSONB NOT NULL,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    estimated_delivery_at TIMESTAMPTZ,
    condition VARCHAR(80)
) PARTITION BY RANGE (timestamp);

CREATE INDEX idx_events_tenant_shipment_time ON shipment_events (tenant_id, shipment_id, timestamp DESC);
CREATE INDEX idx_events_tenant_type_time ON shipment_events (tenant_id, event_type, timestamp DESC);
CREATE INDEX idx_events_location_gin ON shipment_events USING GIN (location);
CREATE INDEX idx_events_metadata_gin ON shipment_events USING GIN (metadata);

CREATE TABLE shipment_events_2026_04 PARTITION OF shipment_events
    FOR VALUES FROM ('2026-04-01') TO ('2026-05-01');

CREATE TABLE webhooks (
    webhook_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(80) NOT NULL,
    target_url VARCHAR(500) NOT NULL,
    secret VARCHAR(200) NOT NULL,
    event_types VARCHAR(500) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_webhooks_tenant_active ON webhooks (tenant_id, active);

CREATE TABLE webhook_delivery_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(80) NOT NULL,
    webhook_id UUID NOT NULL REFERENCES webhooks (webhook_id),
    event_id VARCHAR(80) NOT NULL,
    status_code INTEGER NOT NULL,
    delivery_status VARCHAR(40) NOT NULL,
    attempted_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_delivery_logs_tenant_webhook ON webhook_delivery_logs (tenant_id, webhook_id);
CREATE INDEX idx_delivery_logs_event ON webhook_delivery_logs (event_id);

CREATE TABLE api_rate_limits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(80) NOT NULL,
    window_start TIMESTAMPTZ NOT NULL,
    request_count INTEGER NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, window_start)
);

CREATE INDEX idx_rate_limits_tenant_window ON api_rate_limits (tenant_id, window_start);
