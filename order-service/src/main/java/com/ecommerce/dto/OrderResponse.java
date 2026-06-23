package com.ecommerce.dto;

import com.ecommerce.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderResponse {

    private Integer id;
    private Integer userId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
}
