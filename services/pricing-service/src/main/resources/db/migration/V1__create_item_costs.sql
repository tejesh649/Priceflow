CREATE TABLE item_costs (
    id BIGSERIAL PRIMARY KEY,
    request_id VARCHAR(50) NOT NULL UNIQUE,
    vendor_id VARCHAR(50) NOT NULL,
    item_number BIGINT NOT NULL,
    cost NUMERIC(12,2) NOT NULL,
    effective_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    event_id VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX ux_item_costs_active_vendor_item
    ON item_costs(vendor_id, item_number)
    WHERE status = 'ACTIVE';
