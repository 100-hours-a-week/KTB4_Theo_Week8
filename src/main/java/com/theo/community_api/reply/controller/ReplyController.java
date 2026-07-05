package com.theo.community_api.reply.controller;

import com.theo.community_api.auth.security.CustomUserDetails;
import com.theo.community_api.common.ApiResponse;
import com.theo.community_api.reply.dto.ReplyCreateRequest;
import com.theo.community_api.reply.dto.ReplyCreateResponse;
import com.theo.community_api.reply.dto.ReplyUpdateRequest;
import com.theo.community_api.reply.service.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/comments/{commentId}/replies")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    // 대댓글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<ReplyCreateResponse>> createReply(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody ReplyCreateRequest request
    ) {
        Long replyId = replyService.createReply(userDetails.getUserId(), postId, commentId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        "reply_create_success",
                        new ReplyCreateResponse(replyId)
                ));
    }

    // 대댓글 수정
    @PatchMapping("/{replyId}")
    public ResponseEntity<ApiResponse<Void>> updateReply(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @PathVariable Long replyId,
            @Valid @RequestBody ReplyUpdateRequest request
    ) {
        replyService.updateReply(userDetails.getUserId(), postId, commentId, replyId, request);

        return ResponseEntity
                .ok(ApiResponse.of("reply_modify_success"));
    }

    // 대댓글 삭제
    @DeleteMapping("/{replyId}")
    public ResponseEntity<ApiResponse<Void>> deleteReply(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @PathVariable Long replyId
    ) {
        replyService.deleteReply(userDetails.getUserId(), postId, commentId, replyId);

        return ResponseEntity
                .ok(ApiResponse.of("reply_delete_success"));
    }
}
