package com.ecommerce.repository;

import com.ecommerce.model.NotificationType;
import com.ecommerce.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notification, Integer> {

List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);

List<Notification> findByTypeOrderByCreatedAtDesc(NotificationType type);

List<Notification> findAllByOrderByCreatedAtDesc();

}