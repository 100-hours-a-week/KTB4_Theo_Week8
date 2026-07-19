package com.theo.community_api.draft.service;

import com.theo.community_api.common.exception.*;
import com.theo.community_api.draft.domain.Draft;
import com.theo.community_api.draft.domain.DraftImage;
import com.theo.community_api.draft.dto.DraftImageResponse;
import com.theo.community_api.draft.dto.DraftRequest;
import com.theo.community_api.draft.dto.DraftResponse;
import com.theo.community_api.draft.dto.DraftSummaryResponse;
import com.theo.community_api.draft.repository.DraftImageRepository;
import com.theo.community_api.draft.repository.DraftRepository;
import com.theo.community_api.post.domain.Post;
import com.theo.community_api.post.domain.PostImage;
import com.theo.community_api.post.repository.PostImageRepository;
import com.theo.community_api.post.repository.PostRepository;
import com.theo.community_api.user.domain.User;
import com.theo.community_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DraftService {

    private final DraftRepository draftRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final DraftImageRepository draftImageRepository;
    private final PostImageRepository postImageRepository;

    // 임시글 생성
    @Transactional
    public DraftResponse createDraft(Long loginUserId, DraftRequest request) {
        // 제목 또는 내용 비어있는 경우
        if (isEmptyDraft(request)) {
            throw new BusinessException(ErrorCode.EMPTY_DRAFT_CONTENT);
        }

        User user = userRepository.findById(loginUserId)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Draft draft = new Draft(user,request.getTitle(), request.getContent());

        Draft savedDraft = draftRepository.save(draft);

        saveDraftImages(savedDraft, request.getImageUrls());

        return toDraftResponse(savedDraft);
    }

    // 내 임시글 목록 조회
    public List<DraftSummaryResponse> readDraftList(Long loginUserId) {
        List<Draft> drafts = draftRepository.findAllByUserIdOrderByUpdatedAtDesc(loginUserId);

        List<DraftSummaryResponse> responses = new ArrayList<>();

        for (Draft draft : drafts) {
            responses.add(DraftSummaryResponse.from(draft));
        }

        return responses;
    }


    // 임시글 단건 조회
    public DraftResponse readDraft(Long loginUserId, Long draftId) {

        Draft draft = draftRepository.findByIdAndUserId(draftId, loginUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAFT_NOT_FOUND));

        return toDraftResponse(draft);
    }

    // 임시글 덮어쓰기
    @Transactional
    public DraftResponse updateDraft(Long loginUserId, Long draftId, DraftRequest request) {
        if (isEmptyDraft(request)) {
            throw new BusinessException(ErrorCode.EMPTY_DRAFT_CONTENT);
        }

        Draft draft = draftRepository.findByIdAndUserId(draftId, loginUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAFT_NOT_FOUND));

        draft.update(request.getTitle(), request.getContent());

        // 덮어쓰기 정책: 기존 이미지 접누 삭제 후 새로운 이미지로 모두 저장
        draftImageRepository.deleteAllByDraftIdInBulk(draft.getId());
        saveDraftImages(draft, request.getImageUrls());

        return toDraftResponse(draft);
    }

    // 임시글 삭제
    @Transactional
    public void deleteDraft(Long loginUserId, Long draftId) {

        Draft draft = draftRepository.findByIdAndUserId(draftId, loginUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAFT_NOT_FOUND));

        draftImageRepository.deleteAllByDraftIdInBulk(draft.getId());
        draftRepository.delete(draft);
    }

    // 임시글 발행
    @Transactional
    public Long publishDraft(Long loginUserId, Long draftId) {
        Draft draft = draftRepository.findByIdAndUserId(draftId, loginUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DRAFT_NOT_FOUND));

        if (isInvalidPublishDraft(draft.getTitle(), draft.getContent())) {
            throw new BusinessException(ErrorCode.DRAFT_PUBLISH_REQUIRED_TITLE_AND_CONTENT);
        }

        Post post = new Post(
                draft.getUser(),
                draft.getTitle(),
                draft.getContent()
        );

        // 저장할 임시글
        Post savedPost = postRepository.save(post);
        // 저장할 임시글 사진들
        List<DraftImage> draftImages = draftImageRepository.findAllByDraftIdOrderByImageOrderAsc(draft.getId());

        for (DraftImage draftImage : draftImages) {
                PostImage postImage = new PostImage(
                    savedPost,
                    draftImage.getImageUrl(),
                    draftImage.getImageOrder()
                );

                postImageRepository.save(postImage);
        }

        // 임시글 이미지 삭제
        draftImageRepository.deleteAllByDraftId(draft.getId());

        // 기존 임시글 삭제
        draftRepository.delete(draft);

        return savedPost.getId();
    }

    private void saveDraftImages(Draft draft, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        List<DraftImage> draftImages = new ArrayList<>();

        for (int i = 0; i < imageUrls.size(); i++) {
            String imageUrl = imageUrls.get(i);

            if (isBlank(imageUrl)) {
                continue;
            }

            DraftImage draftImage = new DraftImage(
                    draft,
                    imageUrl,
                    i + 1
            );

            draftImages.add(draftImage);
        }

        draftImageRepository.saveAll(draftImages);
    }

    private DraftResponse toDraftResponse(Draft draft) {
        List<DraftImage> draftImages =
                draftImageRepository.findAllByDraftIdOrderByImageOrderAsc(draft.getId());

        List<DraftImageResponse> imageResponses = new ArrayList<>();

        for (DraftImage draftImage : draftImages) {
            imageResponses.add(DraftImageResponse.from(draftImage));
        }

        return DraftResponse.from(draft, imageResponses);
    }

    private boolean isEmptyDraft(DraftRequest request) {
        return isBlank(request.getTitle())
                && isBlank(request.getContent())
                && isEmptyImages(request.getImageUrls());
    }

    private boolean isEmptyImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return true;
        }

        for (String imageUrl : imageUrls) {
            if (!isBlank(imageUrl)) {
                return false;
            }
        }

        return true;
    }

    private boolean isInvalidPublishDraft(String title, String content){
        return isBlank(title) || isBlank(content);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
