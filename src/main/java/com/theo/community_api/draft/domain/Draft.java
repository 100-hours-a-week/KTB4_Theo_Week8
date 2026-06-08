package com.theo.community_api.draft.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Draft {

    private Long draftId;
    private Long userId;

    private String title;
    private String content;

    private LocalDateTime updatedAt;

    public Draft(Long draftId, Long userId, String title, String content) {
        this.draftId = draftId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}