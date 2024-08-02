package com.sxf.project.payload;

import com.sxf.project.entity.Notification;
import com.sxf.project.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private Notification notification;

    private List<User> users;
}
