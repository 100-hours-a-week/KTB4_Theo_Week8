package com.theo.community_api.comment.service;

import com.theo.community_api.comment.domain.Comment;
import com.theo.community_api.comment.dto.CommentCreateRequest;
import com.theo.community_api.comment.dto.CommentUpdateRequest;
import com.theo.community_api.comment.repository.CommentRepository;
import com.theo.community_api.common.exception.BusinessException;
import com.theo.community_api.common.exception.ErrorCode;
import com.theo.community_api.post.domain.Post;
import com.theo.community_api.post.repository.PostRepository;
import com.theo.community_api.reply.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;

    // 댓글 작성
    public Long createComment(Long loginUserId, Long postId, CommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        Comment comment = commentRepository.save(postId, loginUserId, request.getContent());
        post.increaseCommentCount();

        return comment.getCommentId();
    }

    // 댓글 수정
    public void updateComment(Long loginUserId, Long postId, Long commentId, CommentUpdateRequest request) {
        // 게시물ID와 댓글ID가 제대로 매핑되어있는지 확인
        Comment comment = commentRepository.findByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 작성자가 아닌 작성자가 수정시도하는 경우
        if (!comment.getUserId().equals(loginUserId)) {
            throw new BusinessException(ErrorCode.COMMENT_UPDATE_FORBIDDEN);
        }

        comment.update(request.getContent());
    }

    // 댓글 삭제
    public void deleteComment(Long loginUserId, Long postId, Long commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        Comment comment = commentRepository.findByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUserId().equals(loginUserId)) {
            throw new BusinessException(ErrorCode.COMMENT_DELETE_FORBIDDEN);
        }
        // 대댓글이 존재하는 댓글이면 상태만 변경하도록 설정
        boolean hasReplies = replyRepository.existsByCommentId(commentId);

        if (hasReplies) {
            comment.deleteKeepReplies();
            return;
        }
        post.decreaseCommentCount();

        commentRepository.deleteById(commentId);
    }
}
