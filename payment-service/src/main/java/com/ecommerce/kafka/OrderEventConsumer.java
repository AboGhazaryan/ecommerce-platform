package com.ecommerce.kafka;

import com.ecommerce.event.OrderEvent;
import com.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {
    private final PaymentService paymentService;

    @KafkaListener(topics = "order-created",groupId = "payment-service-group")
    public void handleOrderEvent(OrderEvent event) {
        log.info("Received order event: orderId={}, userId={}, amount={}",
                event.getOrderId(),
                event.getUserId(),
                event.getTotalPrice());

        paymentService.processPayment(event);

        log.info("Payment processed for orderId={}", event.getOrderId());
    }
}
