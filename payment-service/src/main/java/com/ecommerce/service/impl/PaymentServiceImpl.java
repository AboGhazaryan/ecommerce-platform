package com.ecommerce.service.impl;

import com.ecommerce.dto.PaymentResponse;
import com.ecommerce.event.OrderEvent;
import com.ecommerce.exception.PaymentNotFoundException;
import com.ecommerce.kafka.PaymentEventProducer;
import com.ecommerce.mapper.PaymentMapper;
import com.ecommerce.model.entity.Payment;
import com.ecommerce.model.entity.PaymentStatus;
import com.ecommerce.repository.PaymentRepository;
import com.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentEventProducer paymentEventProducer;

    @Override
    @Transactional
    public PaymentResponse processPayment(OrderEvent event) {
        Optional<Payment> existing = paymentRepository.findByOrderId(event.getOrderId());
        if (existing.isPresent()) {
            log.warn("Payment already exists for orderId={}, skipping duplicate", event.getOrderId());
            return paymentMapper.toResponse(existing.get());
        }

        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .userEmail(event.getUserEmail())
                .amount(event.getTotalPrice())
                .status(PaymentStatus.COMPLETED)
                .build();
        paymentRepository.save(payment);
        paymentEventProducer.sendPaymentEvent(payment);
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Integer id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Integer orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByUserId(Integer userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponse refundPayment(Integer id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }
}
