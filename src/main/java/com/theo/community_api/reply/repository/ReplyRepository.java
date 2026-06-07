package com.theo.community_api.reply.repository;

import com.theo.community_api.reply.domain.Reply;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ReplyRepository {
    private final Map<Long, Reply> replyRepository = new HashMap<>();

    // 특정 댓글 ID에 따른 대댓글 ID 리스트 저장소
    private final Map<Long, List<Long>> commentReplyIndex = new HashMap<>();
    private long sequenceIndex = 1L;

    // 대댓글 객체 저장, 댓글ID에 매핑되는 대댓글ID 리스트 저장
    public Reply save(Long commentId, Long postId, Long userId, String content){
        Long replyId = sequenceIndex++;
        Reply reply = new Reply(replyId, postId, commentId, userId, content);
        replyRepository.put(replyId, reply);
        List<Long> replyIds = commentReplyIndex.get(commentId);
        if(replyIds==null){
            replyIds = new ArrayList<>();
            commentReplyIndex.put(commentId, replyIds);
        }
        replyIds.add(replyId);
        return reply;
    }

    // 대댓글 ID로 대댓글 찾기
    public Optional<Reply> findById(Long replyId){
        return Optional.ofNullable(replyRepository.get(replyId));
    }

    // 댓글 ID에 매핑되는 대댓글 ID 리스트 반환하기
    public List<Reply> findAllByCommentId(Long commentId){
        List<Long> replyIds = commentReplyIndex.getOrDefault(commentId, new ArrayList<>());
        List<Reply> replies = new ArrayList<>();
        for(Long replyId : replyIds){
            Reply reply = replyRepository.get(replyId);
            if(reply!=null){
                replies.add(reply);
            }
        }
        return replies;
    }

    // 특정 대댓글 삭제
    public void deleteById(Long replyId){
        Reply reply = replyRepository.remove(replyId);
        if(reply == null){
            return;
        }
        // 대댓글 ID 리스트에서 해당 대댓글 ID 삭제
        List<Long> replyIds = commentReplyIndex.get(reply.getCommentId());
        if(replyIds != null) {
            replyIds.remove(replyId);
            if(replyIds.isEmpty()){
                commentReplyIndex.remove(reply.getCommentId());
            }
        }
    }

    // postId, commentId, replyId가 모두 일치하는 대댓글 찾기
    public Optional<Reply> findByPostIdAndCommentIdAndReplyId(Long postId, Long commentId, Long replyId) {
        Reply reply = replyRepository.get(replyId);

        if (reply == null) {
            return Optional.empty();
        }

        if (!reply.getPostId().equals(postId)) {
            return Optional.empty();
        }

        if (!reply.getCommentId().equals(commentId)) {
            return Optional.empty();
        }

        return Optional.of(reply);
    }


    public boolean existsByCommentId(Long commentId) {
        // 대댓글 ID 리스트에서 해당 댓글에 해당하는 대댓글이 존재하는지 확인
        List<Long> replyIds = commentReplyIndex.get(commentId);
        return replyIds != null && !replyIds.isEmpty();
    }
}
