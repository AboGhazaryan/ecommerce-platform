package com.ecommerce.endpoint;

import com.ecommerce.dto.NotificationResponse;
import com.ecommerce.model.NotificationType;
import com.ecommerce.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationEndpoint {
    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(
            @PathVariable Integer userId,
            @RequestHeader(value = "X-UserService-UserId", required = false) Integer currentUserId,
            @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        if (!"ADMIN".equals(role) && !userId.equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByType(
            @PathVariable NotificationType type,
            @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(notificationService.getNotificationsByType(type));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(
            @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Integer id,
            @RequestHeader(value = "X-UserService-Role", required = false) String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
