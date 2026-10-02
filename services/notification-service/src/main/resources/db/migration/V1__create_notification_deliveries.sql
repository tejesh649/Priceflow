CREATE TABLE notification_deliveries (
    event_id VARCHAR(100) PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    request_id VARCHAR(100) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL,
    sent_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
