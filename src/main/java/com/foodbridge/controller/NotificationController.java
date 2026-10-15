package com.foodbridge.controller;

import com.foodbridge.entity.Notification;
import com.foodbridge.entity.User;
import com.foodbridge.service.NotificationService;
import com.foodbridge.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/notifications")
    public String notificationsPage(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        List<Notification> notifications = notificationService.getUserNotifications(currentUser);
        model.addAttribute("notifications", notifications);
        model.addAttribute("currentUser", currentUser);
        return "notifications/index";
    }

    @PostMapping("/api/notifications/read/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable("id") Long id) {
        User currentUser = userService.getCurrentUser();
        Map<String, Object> response = new HashMap<>();
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }
        notificationService.markAsRead(id, currentUser);
        response.put("success", true);
        response.put("unreadCount", notificationService.getUnreadCount(currentUser));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/notifications/read-all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        User currentUser = userService.getCurrentUser();
        Map<String, Object> response = new HashMap<>();
        if (currentUser == null) {
            response.put("success", false);
            return ResponseEntity.status(401).body(response);
        }
        notificationService.markAllAsRead(currentUser);
        response.put("success", true);
        response.put("unreadCount", 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/notifications/unread-count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUnreadCount() {
        User currentUser = userService.getCurrentUser();
        Map<String, Object> response = new HashMap<>();
        long count = currentUser != null ? notificationService.getUnreadCount(currentUser) : 0;
        response.put("unreadCount", count);
        return ResponseEntity.ok(response);
    }
}
