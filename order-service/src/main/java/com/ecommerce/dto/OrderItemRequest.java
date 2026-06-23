package com.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class
OrderItemRequest implements Serializable {

    @NotNull(message = "productId can't be null")
    private Integer productId;

    @Min(value = 1, message = "quantity must be at least 1")
    private int quantity;
}
