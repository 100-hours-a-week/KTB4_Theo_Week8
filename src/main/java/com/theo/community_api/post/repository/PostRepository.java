package com.theo.community_api.post.repository;

import com.theo.community_api.post.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PostRepository {
    private final Map<Long, Post> postRepository = new HashMap<>();
    private long sequenceIndex = 1L;

    // 새로운 글 추가
    public Post save(Long userId, String title, String content, String postImage){
        Long postId = sequenceIndex++;
        Post post = new Post(postId, userId, title, content, postImage);
        postRepository.put(postId, post);
        return post;
    }

    // 게시글 목록 조회
    public List<Post> findAll(){
        return new ArrayList<>(postRepository.values());
    }

    // 게시글 상세 조회
    public Optional<Post> findById(Long postId){
        return Optional.ofNullable(postRepository.get(postId));
    }

    // 게시글 삭제
    public void deleteById(Long postId){
        Post post = postRepository.get(postId);

        if(post==null){
            return;
        }

        post.delete();
    }
}
