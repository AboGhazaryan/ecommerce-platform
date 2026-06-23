package com.ecommerce.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductEvent {
    private Integer productId;
    private String productName;
    private Integer userId;
    private String eventType;
}
