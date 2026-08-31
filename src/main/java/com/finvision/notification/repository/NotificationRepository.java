package com.finvision.notification.repository;

import com.finvision.notification.entity.Notification;
import com.finvision.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.finvision.notification.entity.NotificationType;


import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(
            User user
    );

    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(
            User user
    );

    long countByUserAndIsReadFalse(
            User user
    );

    boolean existsByUserAndTypeAndMessage(
            User user,
            NotificationType type,
            String message
    );
}