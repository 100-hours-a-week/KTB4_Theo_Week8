package com.theo.community_api.draft.controller;

import com.theo.community_api.auth.service.AuthService;
import com.theo.community_api.common.ApiResponse;
import com.theo.community_api.draft.dto.DraftRequest;
import com.theo.community_api.draft.dto.DraftResponse;
import com.theo.community_api.draft.service.DraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/draft")
@RequiredArgsConstructor
public class DraftController {
    private final DraftService draftService;
    private final AuthService authService;

    // 임시글 생성
    @PostMapping
    public ResponseEntity<ApiResponse<DraftResponse>> craftDraft(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @RequestBody DraftRequest request
    ){
        Long loginUserId = authService.getLoginUserId(sessionId);
        DraftResponse response = draftService.createDraft(loginUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("draft_create_success",response));
    }

    // 내 임시글 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<DraftResponse>>> readMyDrafts(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId
    ) {
        Long loginUserId = authService.getLoginUserId(sessionId);

        List<DraftResponse> response = draftService.readDraftList(loginUserId);

        return ResponseEntity.ok(ApiResponse.of("draft_list_read_success", response));
    }

    // 임시글 조회
    @GetMapping("/{draftId}")
    public ResponseEntity<ApiResponse<DraftResponse>> readDraft(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long draftId
    ){
        long loginUserId = authService.getLoginUserId(sessionId);
        DraftResponse response = draftService.readDraft(loginUserId, draftId);
        return ResponseEntity.ok(ApiResponse.of("draft_read_success",response));
    }

    // 임시글 덮어쓰기
    @PutMapping("/{draftId}")
    public ResponseEntity<ApiResponse<DraftResponse>> updateDraft(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long draftId,
            @RequestBody DraftRequest request
    ){
        Long loginUserId = authService.getLoginUserId(sessionId);
        DraftResponse response = draftService.updateDraft(loginUserId, draftId, request);
        return ResponseEntity.ok(ApiResponse.of("draft_update_success",response));
    }

    // 임시글 삭제
    @DeleteMapping("/{draftId}")
    public ResponseEntity<ApiResponse<Void>> deleteDraft(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long draftId
    ){
        Long loginUserId = authService.getLoginUserId(sessionId);
        draftService.deleteDraft(loginUserId, draftId);

        return ResponseEntity.ok(ApiResponse.of("draft_delete_success"));
    }

    // 임시글 발행
    @PostMapping("/{draftId}/publish")
    public ResponseEntity<ApiResponse<Long>> publishDraft(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @PathVariable Long draftId
    ) {
        Long loginUserId = authService.getLoginUserId(sessionId);

        Long postId = draftService.publishDraft(loginUserId, draftId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("draft_publish_success", postId));
    }
}
