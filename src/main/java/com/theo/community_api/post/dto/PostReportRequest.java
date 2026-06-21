package com.theo.community_api.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostReportRequest {

    @NotBlank(message = "blank_report_reason")
    private String reason;
}