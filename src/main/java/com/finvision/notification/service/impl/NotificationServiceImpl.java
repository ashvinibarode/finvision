package com.finvision.notification.service.impl;

import com.finvision.common.exception.ResourceNotFoundException;
import com.finvision.notification.dto.NotificationResponse;
import com.finvision.notification.entity.Notification;
import com.finvision.notification.repository.NotificationRepository;
import com.finvision.notification.service.NotificationService;
import com.finvision.user.entity.User;
import com.finvision.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.finvision.notification.entity.NotificationType;


import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;


    @Override
    public List<NotificationResponse> getNotifications(
            String email) {

        User user = getUser(email);

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public List<NotificationResponse> getUnreadNotifications(
            String email) {

        User user = getUser(email);

        return notificationRepository
                .findByUserAndIsReadFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public long getUnreadCount(
            String email) {

        User user = getUser(email);

        return notificationRepository
                .countByUserAndIsReadFalse(user);
    }


    @Override
    public void markAsRead(
            String email,
            Long notificationId) {

        User user = getUser(email);

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found"
                                ));

        if (!notification.getUser()
                .getId()
                .equals(user.getId())) {

            throw new ResourceNotFoundException(
                    "Notification not found"
            );
        }

        notification.setIsRead(true);

        notificationRepository.save(notification);
    }


    @Override
    public void markAllAsRead(
            String email) {

        User user = getUser(email);

        List<Notification> notifications =
                notificationRepository
                        .findByUserAndIsReadFalseOrderByCreatedAtDesc(
                                user
                        );

        notifications.forEach(
                notification ->
                        notification.setIsRead(true)
        );

        notificationRepository.saveAll(notifications);
    }


    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }


    private NotificationResponse mapToResponse(
            Notification notification) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    @Override
    public void createNotification(
            User user,
            String title,
            String message,
            NotificationType type) {

        boolean exists =
                notificationRepository
                        .existsByUserAndTypeAndMessage(
                                user,
                                type,
                                message
                        );

        if (exists) {
            return;
        }

        Notification notification =
                Notification.builder()
                        .user(user)
                        .title(title)
                        .message(message)
                        .type(type)
                        .isRead(false)
                        .build();

        notificationRepository.save(notification);
    }
}