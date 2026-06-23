package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Integer productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private String sellerName;
    private String sellerSurname;
}