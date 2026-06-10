package com.theo.community_api.reply.service;

import com.theo.community_api.comment.domain.Comment;
import com.theo.community_api.comment.repository.CommentRepository;
import com.theo.community_api.common.exception.BusinessException;
import com.theo.community_api.common.exception.ErrorCode;
import com.theo.community_api.reply.domain.Reply;
import com.theo.community_api.reply.dto.ReplyCreateRequest;
import com.theo.community_api.reply.dto.ReplyUpdateRequest;
import com.theo.community_api.reply.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;

    // 대댓글 작성
    public Long createReply(Long loginUserId, Long postId, Long commentId, ReplyCreateRequest request) {
        // 게시물ID, 댓글ID, 대댓글ID 모두 일치하는지 확인
        Comment comment = commentRepository.findByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        Reply reply = replyRepository.save(commentId, postId, loginUserId, request.getContent());

        return reply.getReplyId();
    }

    // 대댓글 수정
    public void updateReply(Long loginUserId, Long postId, Long commentId, Long replyId, ReplyUpdateRequest request) {
        Reply reply = replyRepository.findByPostIdAndCommentIdAndReplyId(postId, commentId, replyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPLY_NOT_FOUND));

        if (!reply.getUserId().equals(loginUserId)) {
            throw new BusinessException(ErrorCode.REPLY_MODIFY_FORBIDDEN);
        }

        reply.update(request.getContent());
    }

    // 대댓글 삭제
    public void deleteReply(Long loginUserId, Long postId, Long commentId, Long replyId) {
        Reply reply = replyRepository.findByPostIdAndCommentIdAndReplyId(postId, commentId, replyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPLY_NOT_FOUND));

        if (!reply.getUserId().equals(loginUserId)) {
            throw new BusinessException(ErrorCode.REPLY_DELETE_FORBIDDEN);
        }

        replyRepository.deleteById(replyId);
    }
}