package com.ecommerce.dto;

import com.ecommerce.model.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse implements Serializable {

    private Integer id;
    private Integer orderId;
    private Integer userId;
    private String userEmail;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}
