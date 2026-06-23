package com.ecommerce.dto;

import com.ecommerce.model.ProductCategory;
import com.ecommerce.model.ProductStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ProductResponse {

    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private ProductCategory category;
    private List<String> imageUrls;
    private Integer userId;
    private ProductStatus status;
    private LocalDateTime createdAt;
}
