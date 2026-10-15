package com.foodbridge.service;

import com.foodbridge.entity.Notification;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.NotificationType;
import com.foodbridge.exception.ResourceNotFoundException;
import com.foodbridge.exception.UnauthorizedException;
import com.foodbridge.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification sendNotification(User recipient, String title, String message, NotificationType type, String linkUrl) {
        if (recipient == null) return null;
        Notification notif = new Notification(recipient, title, message, type, linkUrl);
        return notificationRepository.save(notif);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(User recipient) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(User recipient) {
        if (recipient == null) return 0;
        return notificationRepository.countByRecipientAndReadFalse(recipient);
    }

    public void markAsRead(Long id, User currentUser) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
        if (!notification.getRecipient().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You cannot mark another user's notification as read.");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(User currentUser) {
        List<Notification> unread = notificationRepository.findByRecipientAndReadFalseOrderByCreatedAtDesc(currentUser);
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }
}
