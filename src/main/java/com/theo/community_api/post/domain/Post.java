package com.theo.community_api.post.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Post {

    private Long postId;
    private Long userId;

    private String title;
    private String content;
    private String postImage;

    private int likeCount;
    private int commentCount;
    private int viewCount;

    private boolean isEdited;
    private boolean isAuthorDeleted;
    private boolean isBlinded;

    private int reportedCount;

    private LocalDateTime createdAt;

    public Post(Long postId, Long userId, String title, String content, String postImage) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.postImage = postImage;
        this.likeCount = 0;
        this.commentCount = 0;
        this.viewCount = 0;
        this.isEdited = false;
        this.isAuthorDeleted = false;
        this.isBlinded = false;
        this.reportedCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String content, String postImage) {
        this.title = title;
        this.content = content;
        this.postImage = postImage;
        this.isEdited = true;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseReportCount() {
        this.reportedCount++;

        if (this.reportedCount >= 5) {
            this.isBlinded = true;
        }
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount(){
        this.commentCount--;
    }
}