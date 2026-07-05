package com.theo.community_api.reply.repository;

import com.theo.community_api.reply.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("""
        select r
        from Reply r
        join fetch r.user
        join fetch r.comment c
        where c.post.id = :postId
          and r.deletedAt is null
        order by r.id asc
    """) // 게시글 상세조회에서 특정 댓글의 대댓글 목록과 작성자 정보를 함께 조회
    List<Reply> findAllByPostIdWithUserAndComment(@Param("postId") Long postId);

    @Query("""
        select r
        from Reply r
        join fetch r.comment c
        join fetch r.user
        where r.id = :replyId
          and c.id = :commentId
          and c.post.id = :postId
          and r.deletedAt is null
          and c.post.deletedAt is null
    """) // 해당 대댓글이 어떤 게시물, 어떤 댓글에 속해있는지 함께 가져오기 + active한 데이터만 가져오기
    Optional<Reply> findActiveByPostIdAndCommentIdAndReplyId(
            @Param("postId") Long postId,
            @Param("commentId") Long commentId,
            @Param("replyId") Long replyId
    );
}
