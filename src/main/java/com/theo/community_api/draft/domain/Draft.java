package com.theo.community_api.draft.domain;

import com.theo.community_api.common.BaseTimeEntity;
import com.theo.community_api.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "draft")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Draft extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user; // 한 사용자가 여러 임시글을 가짐

    @Column (length = 26, nullable = true)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String content;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Draft(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}