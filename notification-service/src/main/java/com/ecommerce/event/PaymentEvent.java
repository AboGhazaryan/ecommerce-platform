package com.ecommerce.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEvent implements Serializable {

    private Integer paymentId;
    private Integer orderId;
    private Integer userId;
    private String userEmail;
    private BigDecimal amount;
    private String status;
}
