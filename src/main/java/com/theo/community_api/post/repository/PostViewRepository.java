package com.theo.community_api.post.repository;

import com.theo.community_api.post.domain.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
    Optional<PostView> findByPostIdAndUserId(Long postId, Long userId);
}
