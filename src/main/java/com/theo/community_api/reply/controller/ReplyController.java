package com.theo.community_api.reply.controller;

import com.theo.community_api.auth.service.AuthService;
import com.theo.community_api.common.ApiResponse;
import com.theo.community_api.reply.dto.ReplyCreateRequest;
import com.theo.community_api.reply.dto.ReplyCreateResponse;
import com.theo.community_api.reply.dto.ReplyUpdateRequest;
import com.theo.community_api.reply.service.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/comments/{commentId}/replies")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;
    private final AuthService authService;

    // 대댓글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<ReplyCreateResponse>> createReply(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody ReplyCreateRequest request
    ) {
        Long loginUserId = authService.getLoginUserId(sessionId);
        Long replyId = replyService.createReply(loginUserId, postId, commentId, request);

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
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @PathVariable Long replyId,
            @Valid @RequestBody ReplyUpdateRequest request
    ) {
        Long loginUserId = authService.getLoginUserId(sessionId);
        replyService.updateReply(loginUserId, postId, commentId, replyId, request);

        return ResponseEntity
                .ok(ApiResponse.of("reply_modify_success"));
    }

    // 대댓글 삭제
    @DeleteMapping("/{replyId}")
    public ResponseEntity<ApiResponse<Void>> deleteReply(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @PathVariable Long replyId
    ) {
        Long loginUserId = authService.getLoginUserId(sessionId);
        replyService.deleteReply(loginUserId, postId, commentId, replyId);

        return ResponseEntity
                .ok(ApiResponse.of("reply_delete_success"));
    }
}