package com.example.campus_share.entity;
import com.example.campus_share.entity.CommunityMessage;
import lombok.Data;

@Data
public class CommunityMessageVO extends CommunityMessage {
    private String username;    // 发送者昵称
    private String avatarUrl;      // 发送者头像URL

}
