package com.theo.community_api.post.service;

import com.theo.community_api.comment.domain.Comment;
import com.theo.community_api.comment.repository.CommentRepository;
import com.theo.community_api.common.exception.ForbiddenException;
import com.theo.community_api.common.exception.NotFoundException;
import com.theo.community_api.post.domain.Post;
import com.theo.community_api.post.dto.*;
import com.theo.community_api.post.repository.PostRepository;
import com.theo.community_api.reply.domain.Reply;
import com.theo.community_api.reply.repository.ReplyRepository;
import com.theo.community_api.user.domain.User;
import com.theo.community_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    // 게시글 추가
    public Long createPost(Long loginUserId, PostCreateRequest request) {
        Post post = postRepository.save(
                loginUserId,
                request.getTitle(),
                request.getContent(),
                request.getPostImage()
        );

        return post.getPostId();
    }

    // 게시글 목록 조회
    public PostListResponse readPostList(Long lastPostId, int size) {
        List<Post> allPosts = postRepository.findAll();

        // postId 기준 내림차순 정렬 (최신 게시물부터 나오도록)
        allPosts.sort(new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                return o2.getPostId().compareTo(o1.getPostId());
            }
        });

        List<PostSummaryResponse> posts = new ArrayList<>();

        for (Post post : allPosts) {
            // 추가 조회 시 lastPostId보다 작은 게시글만 조회
            if (lastPostId != null && post.getPostId() >= lastPostId) {
                continue;
            }
            User user = userRepository.findById(post.getUserId()).orElse(null);

            posts.add(PostSummaryResponse.from(post, user));

            // hasNext 확인을 위해 size + 1개까지 조회
            if (posts.size() == size + 1) {
                break;
            }
        }

        boolean hasNext = false;

        // 뒤에 게시물들이 더 남아있으면 확인
        if (posts.size() > size) {
            hasNext = true;

            // 마지막 1개 제거
            posts.remove(posts.size() - 1);
        }
        // 다음에 조회할 첫 게시물 ID
        Long newLastPostId = null;
        if (!posts.isEmpty()) {
            newLastPostId =
                    posts.get(posts.size() - 1).getPostId();
        }

        return new PostListResponse(posts, hasNext, newLastPostId);
    }

    // 게시글 상세 조회
    public PostDetailResponse readPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));

        if (post.isBlinded()) {
            throw new ForbiddenException("post_blinded");
        }

        post.increaseViewCount();

        User postAuthor = userRepository.findById(post.getUserId())
                .orElse(null);

        List<Comment> comments = commentRepository.findAllByPostId(postId);
        List<PostCommentResponse> commentResponses = new ArrayList<>();

        for (Comment comment : comments) { // 댓글 리스트 가져오기
            User commentAuthor = userRepository.findById(comment.getUserId())
                    .orElse(null);

            List<Reply> replies = replyRepository.findAllByCommentId(comment.getCommentId());
            List<PostReplyResponse> replyResponses = new ArrayList<>();

            for (Reply reply : replies) { // 대댓글 리스트 가져오기
                User replyAuthor = userRepository.findById(reply.getUserId())
                        .orElse(null);

                replyResponses.add(
                        PostReplyResponse.from(reply, replyAuthor)
                );
            }

            commentResponses.add(
                    PostCommentResponse.from(comment, commentAuthor, replyResponses)
            );
        }

        return PostDetailResponse.from(post, postAuthor, commentResponses);
    }

    // 게시글 수정
    public PostUpdateResponse updatePost(Long loginUserId, Long postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));
        if (!post.getUserId().equals(loginUserId)) {
            throw new ForbiddenException("post_modify_forbidden");
        }
        post.update(request.getTitle(), request.getContent(), request.getPostImage());

        return new PostUpdateResponse(post.isEdited());
    }

    // 게시글 삭제
    public void deletePost(Long loginUserId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));

        if (!post.getUserId().equals(loginUserId)) {
            throw new ForbiddenException("post_delete_forbidden");
        }

        postRepository.deleteById(postId);
    }

    // 게시글 신고
    public void reportPost(Long loginUserId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));

        post.increaseReportCount();
    }
}