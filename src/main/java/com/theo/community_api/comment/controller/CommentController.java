package com.theo.community_api.comment.controller;

import com.theo.community_api.auth.security.CustomUserDetails;
import com.theo.community_api.comment.dto.CommentCreateRequest;
import com.theo.community_api.comment.dto.CommentCreateResponse;
import com.theo.community_api.comment.dto.CommentUpdateRequest;
import com.theo.community_api.comment.service.CommentService;
import com.theo.community_api.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<CommentCreateResponse>> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request
    ){
        Long commentId = commentService.createComment(userDetails.getUserId(), postId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("comment_create_success", new CommentCreateResponse(commentId)));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        commentService.updateComment(userDetails.getUserId(), postId, commentId, request);

        return ResponseEntity.ok(
                ApiResponse.of("comment_update_success")
        );
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(userDetails.getUserId(), postId, commentId);

        return ResponseEntity.ok(
                ApiResponse.of("comment_delete_success")
        );
    }
}
