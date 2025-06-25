<<<<<<< HEAD
package com.example.campus_share.service.impl;

import com.example.campus_share.entity.Announcement;
import com.example.campus_share.mapper.AnnouncementMapper;
import com.example.campus_share.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl extends AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public boolean saveAnnouncement(Announcement announcement) {
        // 保存公告并返回成功与否
        int rowsAffected = announcementMapper.insertAnnouncement(announcement);
        return rowsAffected > 0; // 如果插入成功，返回 true
    }
}
=======
package com.example.campus_share.service.impl;

import com.example.campus_share.entity.Announcement;
import com.example.campus_share.mapper.AnnouncementMapper;
import com.example.campus_share.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl extends AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public boolean saveAnnouncement(Announcement announcement) {
        // 保存公告并返回成功与否
        int rowsAffected = announcementMapper.insertAnnouncement(announcement);
        return rowsAffected > 0; // 如果插入成功，返回 true
    }
}
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
