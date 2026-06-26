package com.ecommerce.service.impl;

import com.ecommerce.dto.NotificationResponse;
import com.ecommerce.event.OrderEvent;
import com.ecommerce.event.PaymentEvent;
import com.ecommerce.event.ProductEvent;
import com.ecommerce.exception.NotificationNotFoundException;
import com.ecommerce.mapper.NotificationMapper;
import com.ecommerce.model.NotificationType;
import com.ecommerce.model.entity.Notification;
import com.ecommerce.repository.NotificationsRepository;
import com.ecommerce.service.EmailService;
import com.ecommerce.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationsRepository notificationsRepository;
    private final NotificationMapper notificationMapper;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.user-id}")
    private int adminId;

    @Override
    @Transactional
    public void handleOrderEvent(OrderEvent event) {
        String userMessage = "Your order has been created. Order ID: " + event.getOrderId();
        String adminMessage = "New order | Client: " + event.getUserEmail() +
                "| Order ID: " + event.getOrderId() +
                "| Amount: " + event.getTotalPrice();

        NotificationResponse userDto = saveAndConvert(
                buildNotification(event.getUserId(), event.getUserEmail(), userMessage, NotificationType.ORDER_CREATED));

        NotificationResponse adminDto = saveAndConvert(buildNotification(
                adminId, adminEmail, adminMessage, NotificationType.ORDER_CREATED));

        messagingTemplate.convertAndSendToUser(String.valueOf(event.getUserId()), "/queue/notifications", userDto);
        messagingTemplate.convertAndSend("/topic/notifications/admin", adminDto);

        emailService.sendEmail(event.getUserEmail(),
                "The order has been created",
                "Your order has been created․ Order ID: " + event.getOrderId() +
                        " Amount: " + event.getTotalPrice());

        emailService.sendEmail(adminEmail, "New order", adminMessage);
        log.info("Order notification saved for userId={}", event.getUserId());
    }

    @Override
    @Transactional
    public void handlePaymentEvent(PaymentEvent event) {
        boolean isCompleted = "COMPLETED".equals(event.getStatus());

        NotificationType type = isCompleted
                ? NotificationType.PAYMENT_COMPLETED
                : NotificationType.PAYMENT_FAILED;

        String userMessage = isCompleted
                ? "The payment was successful։ Order ID: " + event.getOrderId()
                : "Payment failed։ Order ID: " + event.getOrderId();
        String adminMessage = "Payment " + event.getStatus() +
                " | Client: " + event.getUserEmail() +
                " | Order ID: " + event.getOrderId() +
                " | Amount: " + event.getAmount();

        NotificationResponse userDto = saveAndConvert(
                buildNotification(event.getUserId(), event.getUserEmail(), userMessage, type));

        NotificationResponse adminDto = saveAndConvert(
                buildNotification(adminId, adminEmail, adminMessage, type));

        messagingTemplate.convertAndSendToUser(String.valueOf(event.getUserId()),"/queue/notifications", userDto);
        messagingTemplate.convertAndSend("/topic/notifications/admin", adminDto);

        emailService.sendEmail(event.getUserEmail(), "Payment confirmation", userMessage);
        emailService.sendEmail(adminEmail, "Payment " + event.getStatus(), adminMessage);
        log.info("Payment notifications sent — userId={}, orderId={}, status={}",
                event.getUserId(), event.getOrderId(), event.getStatus());
    }

    @Override
    @Transactional
    public void handleProductEvent(ProductEvent event) {
        boolean isCreated = "CREATED".equals(event.getEventType());

        NotificationType type = isCreated
                ? NotificationType.PRODUCT_CREATED
                : NotificationType.PRODUCT_UPDATED;
        String message = isCreated
                ? "New product submitted: \"" + event.getProductName() + "\" (ID: " + event.getProductId() + ")"
                : "Product updated: \"" + event.getProductName() + "\" (ID: " + event.getProductId() + ")";

        NotificationResponse savedDto = saveAndConvert(
                buildNotification(adminId, adminEmail, message, type));
        messagingTemplate.convertAndSend("/topic/notifications/admin", savedDto);
        log.info("Product notification saved type={} productId={}", type, event.getProductId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByUserId(Integer userId) {
        return notificationsRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByType(NotificationType type) {
        return notificationsRepository.findByTypeOrderByCreatedAtDesc(type)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        return notificationsRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteNotification(Integer id) {
        if (!notificationsRepository.existsById(id)) {
            throw new NotificationNotFoundException("Notification not found: " + id);
        }
        notificationsRepository.deleteById(id);
    }

    private Notification buildNotification(Integer userId, String email,
                                           String message, NotificationType type) {
        return Notification.builder()
                .userId(userId)
                .userEmail(email)
                .message(message)
                .type(type)
                .build();
    }

    private NotificationResponse saveAndConvert(Notification notification) {
        return notificationMapper.toResponse(notificationsRepository.save(notification));
    }
}
