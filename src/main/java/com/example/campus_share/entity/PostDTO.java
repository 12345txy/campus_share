package com.example.campus_share.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class PostDTO {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Long categoryId;
    private String coverImage;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private LocalDateTime createTime;
    private String userAvatarUrl;
    private String userNickname;
    private List<String> tags;
    private List<String> images;
    private boolean isFavorite;
    private boolean isLiked;
    private boolean isFollowing;
    public static List<PostDTO> filterPostDTOsByTags(List<String> tags, List<PostDTO> postDTOList) {
        List<PostDTO> filteredList = new ArrayList<>();
        // 将 tags 转换为 Set 以提高查找效率
        Set<String> targetTagSet = new HashSet<>(tags);

        for (PostDTO postDTO : postDTOList) {
            List<String> postTags = postDTO.getTags();
            if (postTags != null) {
                Set<String> postTagSet = new HashSet<>(postTags);
                boolean allTagsContained = true;
                for (String tag : targetTagSet) {
                    if (!postTagSet.contains(tag)) {
                        allTagsContained = false;
                        break;
                    }
                }
                if (allTagsContained) {
                    filteredList.add(postDTO);
<<<<<<< HEAD
=======
                    System.out.println(postDTO);
>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
                }
            }
        }
        return filteredList;
    }
<<<<<<< HEAD
=======

>>>>>>> 02abc81ad64c5a154a4d68136070072802133db1
}