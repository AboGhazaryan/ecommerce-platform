package com.ecommerce.kafka;

import com.ecommerce.event.PaymentEvent;
import com.ecommerce.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {
private final NotificationService notificationService;

    @KafkaListener(
            topics = "payment-completed",
            groupId = "notification-service-group",
            properties = {"spring.json.value.default.type=com.ecommerce.event.PaymentEvent"}
    )
    public void handlePaymentEvent(PaymentEvent event) {
        log.info("Received payment event: orderId={}", event.getOrderId());
        notificationService.handlePaymentEvent(event);
    }
}
