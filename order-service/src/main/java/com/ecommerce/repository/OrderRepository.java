package com.ecommerce.repository;

import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserId(Integer userId);

    List<Order> findByStatus(OrderStatus status);

}
