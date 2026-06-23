package com.ecommerce.mapper;

import com.ecommerce.dto.NotificationResponse;
import com.ecommerce.model.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);
}
