package com.ecommerce.mapper;

import com.ecommerce.dto.NotificationResponse;
import com.ecommerce.model.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "read", target = "isRead")
    NotificationResponse toResponse(Notification notification);
}
