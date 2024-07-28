package com.sxf.project.controller;

import com.sxf.project.dto.NotificationDTO;
import com.sxf.project.entity.Notification;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/all")
    public ResponseEntity<?> postNotificationToAllUsers(@RequestBody NotificationDTO notificationDTO) {
        notificationService.postNotificationToAllUsers(notificationDTO);
        return ResponseEntity.ok(new ApiResponse("Notification sent to all users",true, null));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> postNotificationToOneUser(@PathVariable Long userId, @RequestBody NotificationDTO notificationDTO) {
        try {
            Notification notification = notificationService.postNotificationToOneUser(userId, notificationDTO);
            return ResponseEntity.ok(new ApiResponse("Notification sent to user",true, notification));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(),false, null));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(new ApiResponse( "All notifications",true, notifications));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getNotificationsForUser(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsForUser(userId);
            return ResponseEntity.ok(new ApiResponse( "User notifications",true, notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(),false, null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, @RequestBody NotificationDTO notificationDTO) {
        try {
            Notification notification = notificationService.updateNotification(id, notificationDTO);
            return ResponseEntity.ok(new ApiResponse( "Notification updated",true, notification));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse( e.getMessage(),false, null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(new ApiResponse("Notification deleted",true, null));
    }
}
