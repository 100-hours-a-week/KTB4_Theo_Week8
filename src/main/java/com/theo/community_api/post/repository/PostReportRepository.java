package com.theo.community_api.post.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class PostReportRepository {

    private final Map<Long, Set<Long>> postReportIndex = new HashMap<>();

    public boolean existsByPostIdAndUserId(Long postId, Long userId) {
        Set<Long> reporterIds = postReportIndex.get(postId);

        if (reporterIds == null) {
            return false;
        }

        return reporterIds.contains(userId);
    }

    public void reportSave(Long postId, Long userId) {
        Set<Long> reporterIds =
                postReportIndex.getOrDefault(postId, new HashSet<>());

        reporterIds.add(userId);

        postReportIndex.put(postId, reporterIds);
    }
}
