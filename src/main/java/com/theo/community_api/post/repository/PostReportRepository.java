package com.theo.community_api.post.repository;

import com.theo.community_api.post.domain.PostReport;
import com.theo.community_api.post.domain.PostReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    long countByPostIdAndStatus(Long postId, PostReportStatus status);

    // 관리자 페이지 신고 목록 조회용 - PENDING, ACCEPTED, REJECTED 상태별 조회
    List<PostReport> findAllByStatusOrderByReportedAtDesc(PostReportStatus status);
}