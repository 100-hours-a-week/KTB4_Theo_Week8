package com.theo.community_api.draft.dto;

import com.theo.community_api.draft.domain.Draft;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DraftSummaryResponse {
    private Long draftId;
    private String title;
    private String content;
    private LocalDateTime updatedAt;

    public static DraftSummaryResponse from(Draft draft) {
        return new DraftSummaryResponse(
                draft.getId(),
                draft.getTitle(),
                draft.getContent(),
                draft.getUpdatedAt()
        );
    }
}
