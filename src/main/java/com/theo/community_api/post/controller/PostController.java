package com.theo.community_api.post.controller;

import com.theo.community_api.auth.security.CustomUserDetails;
import com.theo.community_api.common.ApiResponse;
import com.theo.community_api.post.dto.*;
import com.theo.community_api.post.service.PostReportService;
import com.theo.community_api.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostReportService postReportService;

    // 게시글 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostCreateRequest request
    ) {
        Long postId = postService.createPost(userDetails.getUserId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("post_create_success", postId));
    }

    // 게시글 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<PostListResponse>> readPostList(
            @RequestParam(required = false) Long lastPostId,
            @RequestParam(defaultValue = "10") int size
    ) {
        PostListResponse response = postService.readPostList(lastPostId, size);
        return ResponseEntity
                .ok(ApiResponse.of("post_list_read_success", response));
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> readPostDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId
    ) {
        PostDetailResponse response = postService.readPostDetail(userDetails.getUserId(), postId);
        return ResponseEntity
                .ok(ApiResponse.of("post_read_success", response));
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostUpdateResponse>> updatePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        PostUpdateResponse response = postService.updatePost(userDetails.getUserId(), postId, request);

        return ResponseEntity
                .ok(ApiResponse.of("post_update_success", response));
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId
    ) {
        postService.deletePost(userDetails.getUserId(), postId);

        return ResponseEntity
                .ok(ApiResponse.of("post_delete_success"));
    }

    // 게시글 좋아요 토글
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<PostLikeResponse>> togglePostLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId
    ) {
        PostLikeResponse response = postService.togglePostLike(userDetails.getUserId(), postId);

        return ResponseEntity
                .ok(ApiResponse.of("post_like_success", response));
    }

    // 게시글 신고
    @PostMapping("/{postId}/reports")
    public ResponseEntity<ApiResponse<PostReportResponse>> reportPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostReportRequest request
    ){
        PostReportResponse response =
                postReportService.reportPost(postId, userDetails.getUserId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("post_report_success", response));
    }
}
