package com.ecommerce.kafka;

import com.ecommerce.event.PaymentEvent;
import com.ecommerce.model.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public void sendPaymentEvent(Payment payment) {
        kafkaTemplate.send("payment-completed", PaymentEvent.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .userEmail(payment.getUserEmail())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .build());
        log.info("Payment event sent: orderId={}", payment.getOrderId());
    }

}
