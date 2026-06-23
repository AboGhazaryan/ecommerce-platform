package com.ecommerce.mapper;

import com.ecommerce.dto.OrderItemRequest;
import com.ecommerce.dto.OrderItemResponse;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.model.entity.Order;
import com.ecommerce.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items", ignore = true)
    OrderResponse toResponse(Order order);

    OrderItemResponse toItemResponse(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "price", ignore = true)
    OrderItem toEntity(OrderItemRequest request);
}
