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

    // 임시글 덮어쓰기
    @PutMapping
    public ResponseEntity<ApiResponse<DraftResponse>> updateDraft(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @RequestBody DraftRequest request
    ){
        Long loginUserId = authService.getLoginUserId(sessionId);
        DraftResponse response = draftService.updateDraft(loginUserId, request);
        return ResponseEntity.ok(ApiResponse.of("draft_update_success",response));
    }

    // 임시글 조회
    @GetMapping
    public ResponseEntity<ApiResponse<DraftResponse>> readDraft(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId
    ){
        long loginUserId = authService.getLoginUserId(sessionId);
        DraftResponse response = draftService.readDraft(loginUserId);
        return ResponseEntity.ok(ApiResponse.of("draft_read_success",response));
    }

    // 임시글 삭제
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteDraft(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId
    ){
        Long loginUserId = authService.getLoginUserId(sessionId);
        draftService.deleteDraft(loginUserId);

        return ResponseEntity.ok(ApiResponse.of("draft_delete_success"));
    }
}
