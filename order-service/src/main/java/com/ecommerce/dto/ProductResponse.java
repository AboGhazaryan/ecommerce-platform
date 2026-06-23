package com.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponse {

    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Integer userId;
}