package com.ecommerce.dto;

import com.ecommerce.model.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Integer id;
    private Integer userId;
    private String userEmail;
    private String message;
    private NotificationType type;
    private LocalDateTime createdAt;
}
