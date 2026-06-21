package com.theo.community_api.post.service;

import com.theo.community_api.common.exception.BusinessException;
import com.theo.community_api.common.exception.ErrorCode;
import com.theo.community_api.post.domain.Post;
import com.theo.community_api.post.domain.PostReport;
import com.theo.community_api.post.domain.PostReportStatus;
import com.theo.community_api.post.dto.PostReportProcessRequest;
import com.theo.community_api.post.dto.PostReportRequest;
import com.theo.community_api.post.dto.PostReportResponse;
import com.theo.community_api.post.repository.PostReportRepository;
import com.theo.community_api.post.repository.PostRepository;
import com.theo.community_api.user.domain.User;
import com.theo.community_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostReportService {

    private static final long BLIND_REPORT_COUNT = 5L;

    private final PostReportRepository postReportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<PostReportResponse> readReportList(PostReportStatus status) {
        List<PostReport> postReports =
                postReportRepository.findAllByStatusOrderByReportedAtDesc(status);

        List<PostReportResponse> responses = new ArrayList<>();

        for (PostReport postReport : postReports) {
            responses.add(PostReportResponse.from(postReport));
        }

        return responses;
    }

    @Transactional
    public PostReportResponse reportPost(Long postId, Long loginUserId, PostReportRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (postReportRepository.existsByPostIdAndUserId(postId, loginUserId)) {
            throw new BusinessException(ErrorCode.ALREADY_REPORTED_POST);
        }

        PostReport postReport = PostReport.create(post, user, request.getReason());

        PostReport savedPostReport = postReportRepository.save(postReport);

        return PostReportResponse.from(savedPostReport);
    }

    @Transactional
    public PostReportResponse acceptReport(Long reportId, PostReportProcessRequest request) {
        PostReport postReport = postReportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_REPORT_NOT_FOUND));

        if (!postReport.isPending()) {
            throw new BusinessException(ErrorCode.ALREADY_PROCESSED_REPORT);
        }

        postReport.accept(request.getAdminMemo());

        Long postId = postReport.getPost().getId();

        long acceptedReportCount =
                postReportRepository.countByPostIdAndStatus(postId, PostReportStatus.ACCEPTED);

        if (acceptedReportCount >= BLIND_REPORT_COUNT) { // 해당 게시물
            postReport.getPost().blind();
        }

        return PostReportResponse.from(postReport);
    }

    @Transactional
    public PostReportResponse rejectReport(Long reportId, PostReportProcessRequest request) {
        PostReport postReport = postReportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_REPORT_NOT_FOUND));

        if (!postReport.isPending()) {
            throw new BusinessException(ErrorCode.ALREADY_PROCESSED_REPORT);
        }

        postReport.reject(request.getAdminMemo());

        return PostReportResponse.from(postReport);
    }
}