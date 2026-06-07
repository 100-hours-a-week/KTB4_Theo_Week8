package com.theo.community_api.post.dto;

import com.theo.community_api.post.domain.Post;
import com.theo.community_api.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostDetailResponse { // 게시물 상세조회
    private Long postId;
    private String title;
    private String content;
    private String nickname;
    private String profileImage;
    private String postImage;

    private int likeCount;
    private int commentCount;
    private int viewCount;

    private boolean isEdited;
    private boolean isAuthorDeleted;
    private boolean isBlinded;

    private int reportedCount;

    private List<PostCommentResponse> comments;

    public static PostDetailResponse from(Post post, User user, List<PostCommentResponse> comments) {
        String nickname = "알 수 없음";
        String profileImage = null;
        boolean isAuthorDeleted = true;

        if(user != null && !user.isDeleted()){
            nickname = user.getNickname();
            profileImage = user.getProfileImage();
            isAuthorDeleted = false;
        }

        return new PostDetailResponse(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                nickname,
                profileImage,
                post.getPostImage(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getViewCount(),
                post.isEdited(),
                isAuthorDeleted,
                post.isBlinded(),
                post.getReportedCount(),
                comments
        );
    }
}
