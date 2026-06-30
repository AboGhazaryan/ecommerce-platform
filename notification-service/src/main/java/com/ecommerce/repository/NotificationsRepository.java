package com.ecommerce.repository;

import com.ecommerce.model.NotificationType;
import com.ecommerce.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notification, Integer> {

List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);

List<Notification> findByTypeOrderByCreatedAtDesc(NotificationType type);

List<Notification> findAllByOrderByCreatedAtDesc();

@Modifying(clearAutomatically = true)
@Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId")
void markAllAsRead(@Param("userId") Integer userId);

}