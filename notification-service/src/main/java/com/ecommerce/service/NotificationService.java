package com.ecommerce.service;

import com.ecommerce.dto.NotificationResponse;
import com.ecommerce.event.OrderEvent;
import com.ecommerce.event.PaymentEvent;
import com.ecommerce.event.ProductEvent;
import com.ecommerce.model.NotificationType;

import java.util.List;

public interface NotificationService {

    void handleOrderEvent(OrderEvent orderEvent);

    void handlePaymentEvent(PaymentEvent paymentEvent);

    void handleProductEvent(ProductEvent productEvent);

    List<NotificationResponse> getNotificationsByUserId(Integer userId);

    List<NotificationResponse> getNotificationsByType(NotificationType type);

    List<NotificationResponse> getAllNotifications();

    void deleteNotification(Integer id);

    NotificationResponse markAsRead(Integer id, Integer currentId, String role);

    List<NotificationResponse> markAllAsRead(Integer userId);
}
