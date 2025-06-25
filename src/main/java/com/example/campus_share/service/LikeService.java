package com.example.campus_share.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.campus_share.entity.Like;

import java.time.LocalDateTime;

public interface LikeService extends IService<Like> {
    boolean existsByUserIdAndTargetIdAndType(Long userId, Long targetId, Integer type);
    boolean likeComment(Long commentId, Long userId);

    // 取消点赞
    boolean unlikeComment(Long commentId, Long userId);
<<<<<<< HEAD
    int getCommentLikeCount(Long commentId);
=======
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
}