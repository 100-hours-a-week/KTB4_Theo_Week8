package com.theo.community_api.draft.service;

import com.theo.community_api.common.exception.*;
import com.theo.community_api.draft.domain.Draft;
import com.theo.community_api.draft.dto.DraftRequest;
import com.theo.community_api.draft.dto.DraftResponse;
import com.theo.community_api.draft.repository.DraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DraftService {

    private final DraftRepository draftRepository;

    // 임시글 생성
    public DraftResponse createDraft(Long loginUserId, DraftRequest request) {
        // 제목 또는 내용 비어있는 경우
        if (isEmptyDraft(request.getTitle(), request.getContent())) {
            throw new BusinessException(ErrorCode.EMPTY_DRAFT_CONTENT);
        }

        // 이미 임시글이 있는데 다시 생성하려는 경우
        if (draftRepository.findById(loginUserId).isPresent()) {
            throw new BusinessException(ErrorCode.DRAFT_ALREADY_EXISTS);
        }

        Draft draft = draftRepository.save(loginUserId, request.getTitle(), request.getContent());

        return DraftResponse.from(draft);
    }

    // 임시글 덮어쓰기
    public DraftResponse updateDraft(Long loginUserId, DraftRequest request) {
        if (isEmptyDraft(request.getTitle(), request.getContent())) {
            throw new BusinessException(ErrorCode.EMPTY_DRAFT_CONTENT);
        }

        Draft draft = draftRepository.findById(loginUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAFT_NOT_FOUND));

        draft.update(request.getTitle(), request.getContent());

        return DraftResponse.from(draft);
    }

    // 임시글 조회
    public DraftResponse readDraft(Long loginUserId) {

        Draft draft = draftRepository.findById(loginUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAFT_NOT_FOUND));

        return DraftResponse.from(draft);
    }

    // 임시글 삭제
    public void deleteDraft(Long loginUserId) {

        Draft draft = draftRepository.findById(loginUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAFT_NOT_FOUND));

        draftRepository.deleteById(loginUserId);
    }

    private boolean isEmptyDraft(String title, String content) {
        return isBlank(title) && isBlank(content);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}