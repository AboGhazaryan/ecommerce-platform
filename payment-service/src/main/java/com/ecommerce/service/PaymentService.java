package com.ecommerce.service;

import com.ecommerce.dto.PaymentResponse;
import com.ecommerce.event.OrderEvent;

import java.util.List;

public interface PaymentService {

    PaymentResponse processPayment(OrderEvent event);

    PaymentResponse getPaymentById(Integer id);

    PaymentResponse getPaymentByOrderId(Integer orderId);

    List<PaymentResponse> getPaymentsByUserId(Integer userId);

    PaymentResponse refundPayment(Integer id);

    List<PaymentResponse> getAllPayments();
}
