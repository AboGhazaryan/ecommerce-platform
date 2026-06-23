package com.ecommerce.kafka;

import com.ecommerce.event.ProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventProducer {

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public void sendProductEvent(Integer productId, String productName, Integer userId, String eventType) {
        kafkaTemplate.send("product-events",
                ProductEvent.builder()
                        .productId(productId)
                        .productName(productName)
                        .userId(userId)
                        .eventType(eventType)
                        .build());
        log.info("Product event sent productId={} eventType={}", productId, eventType);
    }
}
