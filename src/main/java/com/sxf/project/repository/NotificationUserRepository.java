package com.sxf.project.repository;


import com.sxf.project.entity.NotificationStatus;
import com.sxf.project.entity.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface NotificationUserRepository extends JpaRepository<NotificationUser, Long> {
    List<NotificationUser> findAllByUserId(Long id);
    List<NotificationUser> findAllByNotificationId(Long id);

    long countByUserIdAndNotification_Status(Long userId, NotificationStatus status);
}
