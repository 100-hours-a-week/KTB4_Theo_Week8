package com.theo.community_api.post.repository;

import com.theo.community_api.post.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    // 게시글 상세 조회 시 image_order id 기준 오름차순으로 조회
    List<PostImage> findAllByPost_IdOrderByImageOrderAsc(Long postId);

    // 게시글 수정/삭제 시 기존 이미지 모두 삭제
    void deleteAllByPost_Id(Long postId);
}
