package com.finvision.notification.service;

import com.finvision.notification.dto.NotificationResponse;

import java.util.List;
import com.finvision.notification.entity.NotificationType;
import com.finvision.user.entity.User;

public interface NotificationService {

    List<NotificationResponse> getNotifications(
            String email
    );

    List<NotificationResponse> getUnreadNotifications(
            String email
    );

    long getUnreadCount(
            String email
    );

    void markAsRead(
            String email,
            Long notificationId
    );

    void markAllAsRead(
            String email
    );

    void createNotification(
            User user,
            String title,
            String message,
            NotificationType type
    );
}