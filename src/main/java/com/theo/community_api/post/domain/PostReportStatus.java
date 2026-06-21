package com.theo.community_api.post.domain;

public enum PostReportStatus {
    PENDING,   // 접수됨, 관리자 검토 전
    ACCEPTED, // 신고 승인
    REJECTED  // 신고 반려
}