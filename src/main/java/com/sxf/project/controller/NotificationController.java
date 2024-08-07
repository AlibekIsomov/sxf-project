package com.sxf.project.controller;

import com.sxf.project.dto.NotificationDTO;
import com.sxf.project.dto.NotificationUpdateDTO;
import com.sxf.project.entity.Filial;
import com.sxf.project.entity.Notification;
import com.sxf.project.entity.NotificationUser;
import com.sxf.project.entity.User;
import com.sxf.project.payload.ApiResponse;
import com.sxf.project.payload.NotificationResponse;
import com.sxf.project.repository.NotificationRepository;
import com.sxf.project.security.CurrentUser;
import com.sxf.project.service.NotificationService;
import com.sxf.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public HttpEntity<?> postNotification(@RequestBody NotificationDTO notificationDTO) {
       ApiResponse apiResponse = notificationService.createNotification(notificationDTO);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, @RequestBody NotificationUpdateDTO notificationDTO) {
        ApiResponse apiResponse = notificationService.updateNotification(id, notificationDTO);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllNotifications() {
        List<NotificationResponse> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(new ApiResponse( "All notifications",true, notifications));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping
    public ResponseEntity<?> getNotificationsForUser(@CurrentUser User user) throws Exception {
        List<NotificationUser> notifications = notificationService.getNotificationsForUser(user.getId());
        return ResponseEntity.ok(new ApiResponse( "User notifications",true, notifications));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countUnreadNotifications(@CurrentUser User user) {
        long count = notificationService.countUnreadNotifications(user.getId());
        return ResponseEntity.ok(count);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        ApiResponse apiResponse = notificationService.deleteNotification(id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }
}
