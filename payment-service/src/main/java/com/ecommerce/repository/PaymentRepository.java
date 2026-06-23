package com.ecommerce.repository;

import com.ecommerce.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Integer> {

    Optional<Payment> findByOrderId(Integer orderId);

    List<Payment> findByUserId(Integer userId);
}
