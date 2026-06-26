package com.ecommerce.endpoint;

import com.ecommerce.dto.PaymentResponse;
import com.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentEndpoint {
    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable("id") Integer id,
            @RequestHeader(value = "X-UserService-UserId", required = false) Integer currentUserId,
            @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        PaymentResponse payment = paymentService.getPaymentById(id);
        if (!"ADMIN".equals(role) && !payment.getUserId().equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable("orderId") Integer orderId,
            @RequestHeader(value = "X-UserService-UserId", required = false) Integer currentUserId,
            @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        PaymentResponse payment = paymentService.getPaymentByOrderId(orderId);
        if (!"ADMIN".equals(role) && !payment.getUserId().equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentByUserid(
            @PathVariable("userId") Integer userId,
            @RequestHeader(value = "X-UserService-UserId", required = false) Integer currentUserId,
            @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        if (!"ADMIN".equals(role) && !userId.equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable("id") Integer id,
            @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(paymentService.refundPayment(id));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments(
            @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}
