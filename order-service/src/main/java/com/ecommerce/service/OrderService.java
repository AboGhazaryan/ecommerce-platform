package com.ecommerce.service;

import com.ecommerce.dto.OrderRequest;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.model.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest orderRequest);

    OrderResponse getOrderById(Integer id);

    List<OrderResponse> getOrdersByUserId(Integer userId);

    List<OrderResponse> getCurrentUserOrders();

    List<OrderResponse> getOrdersByStatus(OrderStatus status);

    OrderResponse updateOrderStatus(Integer id, OrderStatus status);

    void deleteOrderById(Integer id);

    List<OrderResponse> getAllOrders();
}
