package com.theo.community_api.draft.repository;

import com.theo.community_api.draft.domain.DraftImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DraftImageRepository extends JpaRepository<DraftImage, Long> {
    List<DraftImage> findAllByDraftIdOrderByImageOrderAsc(Long draftId);

    void deleteAllByDraftId(Long draftId);
}
