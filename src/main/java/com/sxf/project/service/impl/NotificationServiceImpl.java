package com.sxf.project.service.impl;

import com.sxf.project.dto.NotificationDTO;
import com.sxf.project.dto.NotificationUpdateDTO;
import com.sxf.project.entity.Notification;
import com.sxf.project.entity.NotificationStatus;
import com.sxf.project.entity.NotificationUser;
import com.sxf.project.entity.User;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.payload.NotificationResponse;
import com.sxf.project.repository.NotificationRepository;
import com.sxf.project.repository.NotificationUserRepository;
import com.sxf.project.repository.UserRepository;
import com.sxf.project.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationUserRepository notificationUserRepository;

    @Override
    public ApiResponse createNotification(NotificationDTO notificationDTO) {
        List<User> userList = new ArrayList<>();

        for (Long userID : notificationDTO.getUserIDs()) {
            Optional<User> optionalUser = userRepository.findById(userID);
            if (optionalUser.isEmpty()) {
                return new ApiResponse("Bunaqa idlik user yo'q", false);
            }
            userList.add(optionalUser.get());
        }

        Notification notification = new Notification();
        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notification.setStatus(NotificationStatus.UNREAD);

        for (User user : userList) {
            NotificationUser notificationUser = new NotificationUser();
            notificationUser.setUser(user);
            notificationUser.setNotification(notification);
            notificationUserRepository.save(notificationUser);
        }

        return new ApiResponse("Bildirishonomalar muvaffaqiyatli yuborildi!", true);
    }


    @Override
    public ApiResponse updateNotification(Long id, NotificationUpdateDTO notificationDTO) {
        Optional<Notification> optionalNotification = notificationRepository.findById(id);
        if (optionalNotification.isEmpty()) {
            return new ApiResponse("Bunaqa idlik bildirishnoma yo'q", false);
        }
        Notification notification = optionalNotification.get();
        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notificationRepository.save(notification);
        return new ApiResponse("Bildirishonomalar muvaffaqiyatli yangilandi!", true);
    }


    @Override
    public List<NotificationResponse> getAllNotifications() {
        List<NotificationResponse> notificationResponseList = new ArrayList<>();
        List<Notification> notifications = notificationRepository.findAll();
        for (Notification notification : notifications) {
            List<User> userList = new ArrayList<>();

            List<NotificationUser> notificationUserList = notificationUserRepository.findAllByNotificationId(notification.getId());
            for (NotificationUser notificationUser : notificationUserList) {
                userList.add(notificationUser.getUser());
            }
            notificationResponseList.add(new NotificationResponse(notification, userList));
        }
        return notificationResponseList;
    }

    @Override
    public List<NotificationUser> getNotificationsForUser(Long userId) {
       List<NotificationUser> notificationUserList = notificationUserRepository.findAllByUserId(userId);
       for (NotificationUser notificationUser : notificationUserList) {
           Notification notification = notificationUser.getNotification();
           notification.setStatus(NotificationStatus.READ);
           notificationRepository.save(notification);
       }
       return notificationUserList;
    }

    @Override
    public ApiResponse deleteNotification(Long id) {
        Optional<Notification> optionalNotification = notificationRepository.findById(id);
        if (!optionalNotification.isPresent()) {
            return new ApiResponse("Bunday Idlik bildirishnoma yo'q", false);
        }
        Notification notification = optionalNotification.get();
        notificationRepository.deleteById(id);
        return new ApiResponse("Bildirishnoma muvafaqqiyatli o'chirildi", true, notification);
    }
}
