package com.theo.community_api.post.dto;

import com.theo.community_api.post.domain.PostReport;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostReportResponse {

    private final Long reportId;
    private final Long postId;
    private final Long userId;
    private final String reason;
    private final String status;
    private final LocalDateTime reportedAt;
    private final LocalDateTime processedAt;
    private final String adminMemo;

    private PostReportResponse(
            Long reportId,
            Long postId,
            Long userId,
            String reason,
            String status,
            LocalDateTime reportedAt,
            LocalDateTime processedAt,
            String adminMemo
    ) {
        this.reportId = reportId;
        this.postId = postId;
        this.userId = userId;
        this.reason = reason;
        this.status = status;
        this.reportedAt = reportedAt;
        this.processedAt = processedAt;
        this.adminMemo = adminMemo;
    }

    public static PostReportResponse from(PostReport postReport) {
        return new PostReportResponse(
                postReport.getId(),
                postReport.getPost().getId(),
                postReport.getUser().getId(),
                postReport.getReason(),
                postReport.getStatus().name(),
                postReport.getReportedAt(),
                postReport.getProcessedAt(),
                postReport.getAdminMemo()
        );
    }
}