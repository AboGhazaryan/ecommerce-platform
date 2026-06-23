package com.ecommerce.kafka;

import com.ecommerce.event.OrderEvent;
import com.ecommerce.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {
    private final NotificationService notificationService;

    @KafkaListener(
            topics = "order-created",
            groupId = "notification-service-group",
            properties = {"spring.json.value.default.type=com.ecommerce.event.OrderEvent"}
    )
    public void handleOrderEvent(OrderEvent event) {
        log.info("Received order event: orderId={} ", event.getOrderId());
               notificationService.handleOrderEvent(event);
    }
}
