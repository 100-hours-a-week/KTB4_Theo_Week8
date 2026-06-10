package com.theo.community_api.comment.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Comment {

    private Long commentId;
    private Long postId;
    private Long userId;

    private String content;

    private boolean isCommentDeleted;
    private boolean isDeletedByPost;

    private LocalDateTime createdAt;

    public Comment(Long commentId, Long postId, Long userId, String content) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.isCommentDeleted = false;
        this.isDeletedByPost = false;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String content) {
        this.content = content;
    }

    public void deleteKeepReplies() { // 대댓글이 존재하는 경우에는 삭제되어서는 안되므로
        this.content = "삭제된 댓글입니다.";
        this.isCommentDeleted = true;
    }

    public void deleteByPostDeleted(){
        this.isDeletedByPost = true;
    }
}
