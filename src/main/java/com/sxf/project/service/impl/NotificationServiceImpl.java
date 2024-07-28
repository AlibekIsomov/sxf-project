package com.sxf.project.service.impl;

import com.sxf.project.dto.NotificationDTO;
import com.sxf.project.entity.Notification;
import com.sxf.project.entity.User;
import com.sxf.project.repository.NotificationRepository;
import com.sxf.project.repository.UserRepository;
import com.sxf.project.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public Notification createNotification(NotificationDTO notificationDTO, User recipient) {
        Notification notification = new Notification();
        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notification.setRecipient(recipient);
        notification.setTimestamp(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    @Transactional
    @Override
    public void postNotificationToAllUsers(NotificationDTO notificationDTO) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            createNotification(notificationDTO, user);
        }
    }

    @Override
    public Notification postNotificationToOneUser(Long userId, NotificationDTO notificationDTO) throws Exception {
        User recipient = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
        return createNotification(notificationDTO, recipient);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getNotificationsForUser(Long userId) throws Exception {
        User recipient = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
        return notificationRepository.findByRecipient(recipient);
    }

    @Override
    public Notification updateNotification(Long id, NotificationDTO notificationDTO) throws Exception {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new Exception("Notification not found"));
        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        return notificationRepository.save(notification);
    }

    @Override
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
