package com.ecommerce.mapper;


import com.ecommerce.dto.PaymentResponse;
import com.ecommerce.model.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentResponse toResponse(Payment payment);
}
