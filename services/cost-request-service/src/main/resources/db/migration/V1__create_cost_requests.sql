CREATE TABLE cost_requests (
    request_id VARCHAR(50) PRIMARY KEY,
    vendor_id VARCHAR(50) NOT NULL,
    item_number BIGINT NOT NULL,
    proposed_cost NUMERIC(12,2) NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    status VARCHAR(30) NOT NULL,
    effective_date DATE,
    approved_by VARCHAR(100),
    approved_at TIMESTAMP WITH TIME ZONE,
    rejected_by VARCHAR(100),
    rejected_at TIMESTAMP WITH TIME ZONE,
    rejection_reason VARCHAR(1000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT
);

CREATE INDEX idx_cost_requests_status
    ON cost_requests(status);
