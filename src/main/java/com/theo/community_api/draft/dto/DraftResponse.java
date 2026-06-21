package com.theo.community_api.draft.dto;

import com.theo.community_api.draft.domain.Draft;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class DraftResponse {
    private Long draftId;
    private String title;
    private String content;
    private List<DraftImageResponse> imageUrls;
    private LocalDateTime updatedAt;

    public static DraftResponse from(Draft draft, List<DraftImageResponse> imageUrls){
        return new DraftResponse(
                draft.getId(),
                draft.getTitle(),
                draft.getContent(),
                imageUrls,
                draft.getUpdatedAt()
        );
    }
}
