package com.theo.community_api.post;

import com.theo.community_api.common.exception.BusinessException;
import com.theo.community_api.common.exception.ErrorCode;
import com.theo.community_api.post.domain.Post;
import com.theo.community_api.post.repository.*;
import com.theo.community_api.post.service.PostService;
import com.theo.community_api.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock PostRepository postRepository;

    @InjectMocks
    PostService postService;

    @Test
    @DisplayName("작성자가 아닌 사용자가 게시글을 수정하면 POST_MODIFY_FORBIDDEN이 발생한다")
    void updatePost_forbidden_when_user_is_not_author() {
        // given
        User author = new User("author@test.com", "password", "theo", null);
        ReflectionTestUtils.setField(author, "id", 1L);

        Post post = new Post(author, "기존 제목", "기존 내용");
        given(postRepository.findByIdWithUser(1L)).willReturn(Optional.of(post));

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> postService.updatePost(2L, 1L, null)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POST_MODIFY_FORBIDDEN);
    }
}
