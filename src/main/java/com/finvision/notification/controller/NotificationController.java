package com.finvision.notification.controller;

import com.finvision.notification.dto.NotificationResponse;
import com.finvision.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping
    public ResponseEntity<List<NotificationResponse>>
    getNotifications(
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.getNotifications(
                        authentication.getName()
                )
        );
    }


    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>>
    getUnreadNotifications(
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.getUnreadNotifications(
                        authentication.getName()
                )
        );
    }


    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.getUnreadCount(
                        authentication.getName()
                )
        );
    }


    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {

        notificationService.markAsRead(
                authentication.getName(),
                notificationId
        );

        return ResponseEntity.noContent().build();
    }


    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            Authentication authentication) {

        notificationService.markAllAsRead(
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}
