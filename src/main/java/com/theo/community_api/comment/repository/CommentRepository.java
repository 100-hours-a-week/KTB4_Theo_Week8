package com.theo.community_api.comment.repository;

import com.theo.community_api.comment.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CommentRepository {
    private final Map<Long, Comment> commentRepository = new HashMap<>();

    // 특정 게시물ID에 따른 댓글ID 목록
    private final Map<Long, List<Long>> postCommentIndex = new HashMap<>();

    private Long sequenceIndex = 1L;

    public Comment save(Long postId, Long userId, String content){
        Long commentId= sequenceIndex++;
        Comment comment = new Comment(commentId, postId, userId, content);
        commentRepository.put(commentId, comment);
        List<Long> commentIds = postCommentIndex.get(postId);
        if(commentIds == null){
            commentIds = new ArrayList<>();
            postCommentIndex.put(postId, commentIds);
        }

        commentIds.add(commentId); // 댓글 ID 리스트에 추가될 댓글 ID 저장
        return comment;
    }

    // 특정 게시물에 대한 댓글리스트 반환
    public List<Comment> findAllByPostId(Long postId){
        List<Long> commentIds = postCommentIndex.getOrDefault(postId, new ArrayList<>()); // 해당 게시물의 댓글 ID 가져오기
        List<Comment> comments = new ArrayList<>();
        for(Long commentId : commentIds){
            Comment comment = commentRepository.get(commentId);
            if(comment==null){
                continue;
            }

            if(comment.isCommentDeleted()){
                continue;
            }

            comments.add(comment);
        }
        return comments;
    }

    // 특정 게시물 삭제 (댓글 삭제 + 댓글 ID 목록에서 삭제)
    public void deleteById(Long commentId){
        Comment comment = commentRepository.get(commentId); // 삭제할 댓글 가져오기
        if(comment==null){
            return;
        }
        comment.deleteByPostDeleted(); // 대댓글과 함께 안보이도록
    }

    // postId와 commentId가 모두 일치하는 댓글 찾기
    public Optional<Comment> findByPostIdAndCommentId(Long postId, Long commentId) {
        Comment comment = commentRepository.get(commentId);

        if (comment == null) {
            return Optional.empty();
        }

        if (!comment.getPostId().equals(postId)) {
            return Optional.empty();
        }

        return Optional.of(comment);
    }
}
