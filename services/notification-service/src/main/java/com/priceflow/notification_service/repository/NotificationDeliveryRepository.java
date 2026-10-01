package com.priceflow.notification_service.repository;

import com.priceflow.notification_service.model.NotificationDelivery;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface NotificationDeliveryRepository
        extends ReactiveCrudRepository<NotificationDelivery, String> {
}