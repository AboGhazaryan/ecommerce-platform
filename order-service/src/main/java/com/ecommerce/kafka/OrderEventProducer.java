package com.ecommerce.kafka;

import com.ecommerce.dto.UserResponse;
import com.ecommerce.event.OrderEvent;
import com.ecommerce.model.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void sendOrderEvent(Order order, UserResponse user) {
        kafkaTemplate.send("order-created",
                OrderEvent.builder()
                        .orderId(order.getId())
                        .userId(order.getUserId())
                        .userEmail(user.getEmail())
                        .totalPrice(order.getTotalPrice())
                        .status(order.getStatus().name())
                        .build());
        log.info("Order event sent orderId={}",order.getId());
    }
}
