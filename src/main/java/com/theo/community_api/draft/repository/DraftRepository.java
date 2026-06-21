package com.theo.community_api.draft.repository;

import com.theo.community_api.draft.domain.Draft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DraftRepository extends JpaRepository<Draft, Long> {

    // 유저ID에 맞는 임시글 목록 가져오기
    List<Draft> findAllByUserIdOrderByUpdatedAtDesc(Long userId);

    // 해당 draftId가 존재하면서 동시에 로그인한 유저의 임시글인지 확인하기
    Optional<Draft> findByIdAndUserId(Long draftId, Long userId);
}
