package com.theo.community_api.reply.dto;

import com.theo.community_api.common.ValidationConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReplyUpdateRequest {
    @NotBlank(message = "blank_reply_content")
    @Size(max = ValidationConst.REPLY_MAX_LENGTH,
            message = "invalid_reply_format")
    private String content;
}
