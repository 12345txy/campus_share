package com.example.campus_share.controller.admin;
import com.example.campus_share.entity.Announcement;
import com.example.campus_share.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
@RestController
@RequestMapping("/api/admin/system/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    // 发布公告
    @PostMapping("/create")
    public ResponseEntity<String> createAnnouncement(@RequestBody Announcement announcement) {
        boolean success = announcementService.saveAnnouncement(announcement);

        if (success) {
            // 发布成功后推送给所有订阅了 /topic/announcements 的用户
            messagingTemplate.convertAndSend("/topic/announcements", announcement);
            return ResponseEntity.ok("公告发布成功！");
        } else {
            return ResponseEntity.status(500).body("公告发布失败！");
        }
    }
}


