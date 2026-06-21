package com.theo.community_api.post.controller;

import com.theo.community_api.common.ApiResponse;
import com.theo.community_api.post.domain.PostReportStatus;
import com.theo.community_api.post.dto.PostReportProcessRequest;
import com.theo.community_api.post.dto.PostReportResponse;
import com.theo.community_api.post.service.PostReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/post-reports")
public class AdminPostReportController { // 관리자 신고 처리

    private final PostReportService postReportService;

    // 관리자가 신고 리스트 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostReportResponse>>> readReportList(
            @RequestParam PostReportStatus status
    ) {
        List<PostReportResponse> response =
                postReportService.readReportList(status);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("post_report_list_read_success", response));
    }

    // 해당 신고 상태 허가
    @PatchMapping("/{reportId}/accept")
    public ResponseEntity<ApiResponse<PostReportResponse>> acceptReport(
            @PathVariable Long reportId,
            @Valid @RequestBody PostReportProcessRequest request
    ) {
        PostReportResponse response =
                postReportService.acceptReport(reportId, request);

        return ResponseEntity
                .ok(ApiResponse.of("post_report_accept_success", response));
    }

    // 해당 신고 거절
    @PatchMapping("/{reportId}/reject")
    public ResponseEntity<ApiResponse<PostReportResponse>> rejectReport(
            @PathVariable Long reportId,
            @Valid @RequestBody PostReportProcessRequest request
    ) {
        PostReportResponse response =
                postReportService.rejectReport(reportId, request);

        return ResponseEntity
                .ok(ApiResponse.of("post_report_reject_success", response));
    }
}