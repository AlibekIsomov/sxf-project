package com.sxf.project.service;

import com.sxf.project.dto.NotificationDTO;
import com.sxf.project.dto.NotificationUpdateDTO;
import com.sxf.project.entity.Notification;
import com.sxf.project.entity.NotificationUser;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.payload.NotificationResponse;

import java.util.List;

public interface NotificationService {
    ApiResponse createNotification(NotificationDTO notificationDTO);

    List<NotificationResponse> getAllNotifications();

    List<NotificationUser> getNotificationsForUser(Long userId) throws Exception;

    ApiResponse updateNotification(Long id, NotificationUpdateDTO notificationDTO);

    long countUnreadNotifications(Long userId);

    ApiResponse deleteNotification(Long id);
}
