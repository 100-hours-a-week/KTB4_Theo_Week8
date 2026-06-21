package com.theo.community_api.draft.dto;

import com.theo.community_api.draft.domain.DraftImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DraftImageResponse {
    private Long imageId;
    private String imageUrl;
    private Integer imageOrder;

    public static DraftImageResponse from(DraftImage draftImage){
        return new DraftImageResponse(
                draftImage.getId(),
                draftImage.getImageUrl(),
                draftImage.getImageOrder()
        );
    }
}
