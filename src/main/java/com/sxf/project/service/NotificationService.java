package com.sxf.project.service;

import com.sxf.project.dto.NotificationDTO;
import com.sxf.project.entity.Notification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationService {
    @Transactional
    void postNotificationToAllUsers(NotificationDTO notificationDTO);

    Notification postNotificationToOneUser(Long userId, NotificationDTO notificationDTO) throws Exception;

    List<Notification> getAllNotifications();

    List<Notification> getNotificationsForUser(Long userId) throws Exception;

    Notification updateNotification(Long id, NotificationDTO notificationDTO) throws Exception;

    void deleteNotification(Long id);
}
