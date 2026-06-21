package com.theo.community_api.comment.repository;

import com.theo.community_api.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
        select c
        from Comment c
        join fetch c.user
        where c.post.id = :postId
        order by c.id asc
    """) // 게시글 상세 조회 시 댓글 목록 조립 때 사용
    List<Comment> findAllByPostIdWithUser(@Param("postId") Long postId); // 댓글을 조회하면서 user 정보를 함께 가져오기

    @Query("""
        select c
        from Comment c
        join fetch c.post
        join fetch c.user
        where c.id = :commentId
          and c.post.id = :postId
          and c.deletedAt is null
          and c.post.deletedAt is null
    """)
    Optional<Comment> findActiveByPostIdAndCommentId(
            /* 게시물 ID와 댓글 ID에 맞는 댓글을 가져오기
               user 도 join fetch 한 이유? -> 댓글에 작성자 정보도 모두 포함되어야 하므로
               만약 유저 데이터도 같이 안가져오면 N+1 발생..
               post join fetch 한 이유? -> 댓글 삭제 시 해당 게시물 정보(commentCount)도 같이 가져와야 하니까.. */
            @Param("postId") Long postId,
            @Param("commentId") Long commentId
    );
}
