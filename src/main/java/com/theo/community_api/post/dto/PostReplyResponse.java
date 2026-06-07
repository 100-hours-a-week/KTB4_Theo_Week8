package com.theo.community_api.post.dto;

import com.theo.community_api.reply.domain.Reply;
import com.theo.community_api.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostReplyResponse {
    private Long replyId;
    private String nickname;
    private String profileImage;
    private String replyContent;
    private boolean isAuthorDeleted;
    private boolean isReplyDeleted;
    private LocalDateTime createdAt;

    public static PostReplyResponse from(Reply reply, User user) {
        String nickname = "알 수 없음";
        String profileImage = null;
        boolean isAuthorDeleted = true;

        if (user != null && !user.isDeleted()) {
            nickname = user.getNickname();
            profileImage = user.getProfileImage();
            isAuthorDeleted = false;
        }

        return new PostReplyResponse(
                reply.getReplyId(),
                nickname,
                profileImage,
                reply.getContent(),
                isAuthorDeleted,
                reply.isReplyDeleted(),
                reply.getCreatedAt()
        );
    }
}