package com.example.campus_share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campus_share.entity.Announcement;
import org.springframework.stereotype.Repository;
import com.example.campus_share.entity.Announcement;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface AnnouncementMapper {

    // 插入公告
    @Insert("INSERT INTO campus_share.t_announcements(title, content, author, created_at) " +
            "VALUES (#{title}, #{content}, #{author}, #{createdAt})")
    int insertAnnouncement(Announcement announcement);

    // 确认插入成功后返回影响的行数
}

