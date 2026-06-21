package com.theo.community_api.post.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostReportProcessRequest {

    @Size(max = 300, message = "invalid_admin_memo")
    private String adminMemo;
}