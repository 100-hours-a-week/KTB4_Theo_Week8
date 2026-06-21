package com.theo.community_api.post.controller;

import com.theo.community_api.auth.service.AuthService;
import com.theo.community_api.common.ApiResponse;
import com.theo.community_api.post.dto.*;
import com.theo.community_api.post.service.PostReportService;
import com.theo.community_api.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final AuthService authService;
    private final PostReportService postReportService;

    // 게시글 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @Valid @RequestBody PostCreateRequest request
    ) {
        Long loginUserId = authService.getLoginUserId(sessionId);
        Long postId = postService.createPost(loginUserId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("post_create_success", postId));
    }

    // 게시글 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<PostListResponse>> readPostList(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @RequestParam(required = false) Long lastPostId,
            @RequestParam(defaultValue = "10") int size
    ) {
        authService.getLoginUserId(sessionId);
        PostListResponse response = postService.readPostList(lastPostId, size);
        return ResponseEntity
                .ok(ApiResponse.of("post_list_read_success", response));
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> readPostDetail(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long postId
    ) {
        Long loginUserId = authService.getLoginUserId(sessionId);
        PostDetailResponse response = postService.readPostDetail(loginUserId, postId);
        return ResponseEntity
                .ok(ApiResponse.of("post_read_success", response));
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostUpdateResponse>> updatePost(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        Long loginUserId = authService.getLoginUserId(sessionId);
        PostUpdateResponse response = postService.updatePost(loginUserId, postId, request);

        return ResponseEntity
                .ok(ApiResponse.of("post_update_success", response));
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long postId
    ) {
        Long loginUserId = authService.getLoginUserId(sessionId);
        postService.deletePost(loginUserId, postId);

        return ResponseEntity
                .ok(ApiResponse.of("post_delete_success"));
    }

    // 게시글 좋아요 토글
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<PostLikeResponse>> togglePostLike(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long postId
    ) {
        Long loginUserId = authService.getLoginUserId(sessionId);
        PostLikeResponse response = postService.togglePostLike(loginUserId, postId);

        return ResponseEntity
                .ok(ApiResponse.of("post_like_success", response));
    }

    // 게시글 신고
    @PostMapping("/{postId}/reports")
    public ResponseEntity<ApiResponse<PostReportResponse>> reportPost(
            @PathVariable Long postId,
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @Valid @RequestBody PostReportRequest request
    ){
        Long loginUserId = authService.getLoginUserId(sessionId);

        PostReportResponse response =
                postReportService.reportPost(postId, loginUserId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("post_report_success", response));
    }
}