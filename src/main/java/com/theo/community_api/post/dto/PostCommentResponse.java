package com.theo.community_api.post.dto;

import com.theo.community_api.comment.domain.Comment;
import com.theo.community_api.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostCommentResponse {
    private Long commentId;
    private String nickname;
    private String profileImage;
    private String commentContent;
    private boolean isAuthorDeleted;
    private boolean isCommentDeleted;
    private LocalDateTime createdAt;
    private List<PostReplyResponse> replies;

    public static PostCommentResponse from(
            Comment comment,
            User user,
            List<PostReplyResponse> replies
    ) {
        String nickname = "알 수 없음";
        String profileImage = null;
        boolean isAuthorDeleted = true;

        if (user != null && !user.isDeleted()) {
            nickname = user.getNickname();
            profileImage = user.getProfileImage();
            isAuthorDeleted = false;
        }

        String content = comment.getContent();

        if (comment.isCommentDeleted()) {
            content = "삭제된 댓글입니다.";
        }

        return new PostCommentResponse(
                comment.getCommentId(),
                nickname,
                profileImage,
                content,
                isAuthorDeleted,
                comment.isCommentDeleted(),
                comment.getCreatedAt(),
                replies
        );
    }
}
