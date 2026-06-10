package com.theo.community_api.reply.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Reply {

    private Long replyId;
    private Long postId;
    private Long commentId;
    private Long userId;

    private String content;

    private boolean isReplyDeleted;

    private LocalDateTime createdAt;

    public Reply(Long replyId, Long postId, Long commentId, Long userId, String content) {
        this.replyId = replyId;
        this.postId = postId;
        this.commentId = commentId;
        this.userId = userId;
        this.content = content;
        this.isReplyDeleted = false;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String content) {
        this.content = content;
    }

    public void delete(){
        this.isReplyDeleted = true;
    }

    public boolean isDeleted(){
        return isReplyDeleted;
    }
}