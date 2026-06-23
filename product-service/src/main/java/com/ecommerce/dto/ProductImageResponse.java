package com.ecommerce.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageResponse {
    private Integer id;
    private Integer productId;
    private String imageUrl;
    private LocalDateTime createdAt;
}
