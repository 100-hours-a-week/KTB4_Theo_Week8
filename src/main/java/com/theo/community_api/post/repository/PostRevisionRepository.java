package com.theo.community_api.post.repository;

import com.theo.community_api.post.domain.PostRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRevisionRepository extends JpaRepository<PostRevision, Long> {

    // 이후에 수정이력 조회를 위한 jpa 메서드 : 관리자용으로 고려
    List<PostRevision> findAllByPostIdOrderByUpdatedAtDesc(Long postId);
}