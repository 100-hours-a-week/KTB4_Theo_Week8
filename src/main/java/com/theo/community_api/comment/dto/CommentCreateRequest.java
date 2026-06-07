package com.theo.community_api.comment.dto;

import com.theo.community_api.common.ValidationConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {
    @NotBlank(message = "blank_comment_content")
    @Size(max = ValidationConst.COMMENT_MAX_LENGTH,
            message = "invalid_comment_format")
    private String content;
}