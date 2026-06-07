package com.theo.community_api.draft.dto;

import com.theo.community_api.draft.domain.Draft;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DraftResponse {
    private Long draftId;
    private String title;
    private String content;
    private LocalDateTime updatedAt;

    public static DraftResponse from(Draft draft){
        return new DraftResponse(
                draft.getDraftId(),
                draft.getTitle(),
                draft.getContent(),
                draft.getUpdatedAt()
        );
    }
}
