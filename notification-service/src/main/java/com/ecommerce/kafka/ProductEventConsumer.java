package com.ecommerce.kafka;

import com.ecommerce.event.ProductEvent;
import com.ecommerce.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "product-events",
            groupId = "notification-service-group",
            properties = {"spring.json.value.default.type=com.ecommerce.event.ProductEvent"}
    )
    public void handleProductEvent(ProductEvent event) {
        log.info("Received product event: productId={} eventType={}", event.getProductId(), event.getEventType());
        notificationService.handleProductEvent(event);
    }
}
