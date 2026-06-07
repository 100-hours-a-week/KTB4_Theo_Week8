package com.theo.community_api.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostListResponse { // 게시물 목록 조회
    private List<PostSummaryResponse> posts;
    private boolean hasNext;
    private Long lastPostId;
}
