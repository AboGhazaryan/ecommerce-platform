package com.ecommerce.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent implements Serializable {
    private Integer orderId;
    private Integer userId;
    private String userEmail;
    private BigDecimal totalPrice;
    private String status;
}