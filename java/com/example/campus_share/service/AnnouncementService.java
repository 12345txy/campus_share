<<<<<<< HEAD
package com.example.campus_share.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.Announcement;
import com.example.campus_share.mapper.AnnouncementMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    // 保存公告
    public boolean saveAnnouncement(Announcement announcement) {
        // 设置创建时间
        announcement.setCreatedAt(LocalDateTime.now());
        // 插入公告并返回是否成功
        return announcementMapper.insertAnnouncement(announcement) > 0;
    }
}


=======
package com.example.campus_share.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.campus_share.entity.Announcement;
import com.example.campus_share.mapper.AnnouncementMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    // 保存公告
    public boolean saveAnnouncement(Announcement announcement) {
        // 设置创建时间
        announcement.setCreatedAt(LocalDateTime.now());
        // 插入公告并返回是否成功
        return announcementMapper.insertAnnouncement(announcement) > 0;
    }
}


>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
