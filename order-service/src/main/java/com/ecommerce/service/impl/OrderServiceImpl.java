package com.ecommerce.service.impl;

import com.ecommerce.dto.OrderItemRequest;
import com.ecommerce.dto.OrderItemResponse;
import com.ecommerce.dto.OrderRequest;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.feignClient.ProductClient;
import com.ecommerce.feignClient.UserClient;
import com.ecommerce.kafka.OrderEventProducer;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.entity.Order;
import com.ecommerce.model.entity.OrderItem;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.service.OrderService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderEventProducer orderEventProducer;
    private final OrderMapper orderMapper;
    private final UserClient userClient;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, Integer userId) {
        var user = userClient.getSellerInfoById(userId);

        List<OrderItem> items = orderRequest.getItems()
                .stream()
                .map(this::buildOrderItem)
                .toList();

        BigDecimal totalPrice = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .items(items)
                .build();
        items.forEach(item -> item.setOrder(order));
        orderRepository.save(order);
        orderEventProducer.sendOrderEvent(order, user);
        return toEnrichedResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return toEnrichedResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::toEnrichedResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(this::toEnrichedResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Integer id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
        return toEnrichedResponse(order);
    }

    @Override
    @Transactional
    public void deleteOrderById(Integer id) {
        orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getCurrentUserOrders(Integer userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::toEnrichedResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toEnrichedResponse)
                .toList();
    }

    private OrderResponse toEnrichedResponse(Order order) {
        List<OrderItemResponse> enrichedItems = order.getItems().stream()
                .map(this::toEnrichedItemResponse)
                .toList();
        return orderMapper.toResponse(order).toBuilder()
                .items(enrichedItems)
                .build();
    }

    private OrderItemResponse toEnrichedItemResponse(OrderItem item) {
        OrderItemResponse.OrderItemResponseBuilder builder = orderMapper.toItemResponse(item).toBuilder();
        try {
            var product = productClient.getProductById(item.getProductId());
            builder.productName(product.getName());
            if (product.getUserId() != null) {
                try {
                    var seller = userClient.getSellerInfoById(product.getUserId());
                    builder.sellerName(seller.getName())
                            .sellerSurname(seller.getSurname());
                } catch (FeignException e) {
                    log.warn("Could not fetch seller info for userId={}: {}",
                            product.getUserId(), e.getMessage());
                }
            }
        } catch (FeignException.NotFound ignored) {
        }
        return builder.build();
    }

    private OrderItem buildOrderItem(OrderItemRequest itemRequest) {
        var product = productClient.getProductById(itemRequest.getProductId());
        if (product.getQuantity() < itemRequest.getQuantity()) {
            throw new InsufficientStockException(
                    "Not enough stock for product");
        }
        productClient.decreaseStock(itemRequest.getProductId(), itemRequest.getQuantity());
        OrderItem item = orderMapper.toEntity(itemRequest);
        item.setPrice(product.getPrice());
        return item;
    }
}
