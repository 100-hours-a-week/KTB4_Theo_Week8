package com.theo.community_api.reply.service;

import com.theo.community_api.comment.domain.Comment;
import com.theo.community_api.comment.repository.CommentRepository;
import com.theo.community_api.common.exception.ForbiddenException;
import com.theo.community_api.common.exception.NotFoundException;
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
                .orElseThrow(() -> new NotFoundException("comment_not_found"));

        Reply reply = replyRepository.save(commentId, postId, loginUserId, request.getContent());

        return reply.getReplyId();
    }

    // 대댓글 수정
    public void updateReply(Long loginUserId, Long postId, Long commentId, Long replyId, ReplyUpdateRequest request) {
        Reply reply = replyRepository.findByPostIdAndCommentIdAndReplyId(postId, commentId, replyId)
                .orElseThrow(() -> new NotFoundException("reply_not_found"));

        if (!reply.getUserId().equals(loginUserId)) {
            throw new ForbiddenException("reply_modify_forbidden");
        }

        reply.update(request.getContent());
    }

    // 대댓글 삭제
    public void deleteReply(Long loginUserId, Long postId, Long commentId, Long replyId) {
        Reply reply = replyRepository.findByPostIdAndCommentIdAndReplyId(postId, commentId, replyId)
                .orElseThrow(() -> new NotFoundException("reply_not_found"));

        if (!reply.getUserId().equals(loginUserId)) {
            throw new ForbiddenException("reply_delete_forbidden");
        }

        replyRepository.deleteById(replyId);
    }
}