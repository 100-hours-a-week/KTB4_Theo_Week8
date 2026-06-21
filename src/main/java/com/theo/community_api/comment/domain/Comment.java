package com.theo.community_api.comment.domain;

import com.theo.community_api.post.domain.Post;
import com.theo.community_api.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    public Comment(Post post, User user, String content) {
        this.post = post;
        this.user = user;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void deleteKeepReplies() { // 대댓글이 존재하는 경우에는 삭제되어서는 안되므로
        this.content = "삭제된 댓글입니다.";
        this.deletedAt = LocalDateTime.now();
    }

    public void delete(){
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted(){
        return deletedAt != null;
    }
}
