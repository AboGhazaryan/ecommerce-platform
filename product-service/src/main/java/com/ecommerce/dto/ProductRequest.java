package com.ecommerce.dto;

import com.ecommerce.model.ProductCategory;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "name can't be blank")
    private String name;

    @NotBlank(message = "description can't be blank")
    @Size(max = 500, message = "description must not exceed 500 characters")
    private String description;

    @NotNull(message = "price can't be null")
    @DecimalMin(value = "0.01", message = "price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "quantity can't be null")
    @Min(value = 0, message = "quantity must be 0 or more")
    private Integer quantity;

    @NotNull(message = "category can't be null")
    private ProductCategory category;
}
