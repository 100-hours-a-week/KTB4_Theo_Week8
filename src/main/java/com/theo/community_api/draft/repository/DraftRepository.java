package com.theo.community_api.draft.repository;

import com.theo.community_api.draft.domain.Draft;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class DraftRepository {
    private final Map<Long, Draft> draftRepository = new HashMap<>();

    // 임시글 저장
    public Draft save(Long userId, String title, String content){
        Long draftId = userId;
        Draft draft = new Draft(draftId, userId, title, content);
        draftRepository.put(userId,draft);
        return draft;
    }

    // 유저ID에 맞는 임시글 가져오기
    public Optional<Draft> findById(Long userId){
        return Optional.ofNullable(draftRepository.get(userId));
    }

    // 임시글 삭제하기
    public void deleteById(Long userId){
        draftRepository.remove(userId);
    }
}
