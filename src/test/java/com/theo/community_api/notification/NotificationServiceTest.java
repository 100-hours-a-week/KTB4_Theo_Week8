package com.theo.community_api.notification;

import com.theo.community_api.notification.domain.Notification;
import com.theo.community_api.notification.domain.NotificationSourceType;
import com.theo.community_api.notification.domain.NotificationType;
import com.theo.community_api.notification.repository.NotificationRepository;
import com.theo.community_api.notification.service.NotificationService;
import com.theo.community_api.post.domain.Post;
import com.theo.community_api.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @Mock
    private NotificationRepository notificationRepository;

    private NotificationService notificationService;

    private User postAuthor;
    private Post post;
    private User actor;

    @BeforeEach
    void setUp() { // 게시글 작성자, 게시글, 행위자 설정
        notificationService =
                new NotificationService(notificationRepository);

        postAuthor = new User(
                "author@test.com",
                "password",
                "작성자",
                null
        );

        ReflectionTestUtils.setField(postAuthor, "id", 1L);

        post = new Post(
                postAuthor,
                "테스트 제목",
                "테스트 내용"
        );

        ReflectionTestUtils.setField(post, "id", 10L);

        actor = new User(
                "actor@test.com",
                "password",
                "행위자",
                null
        );

        ReflectionTestUtils.setField(actor, "id", 2L);
    }

    @Test
    @DisplayName("자신의 게시글에 좋아요를 누르면 알림을 생성하지 않는다")
    void does_not_create_like_notification_for_own_post() {
        notificationService.createLikeNotification(
                post,
                postAuthor
        );

        verify(notificationRepository, never())
                .save(any(Notification.class));
    }

    @Test
    @DisplayName("다른 사용자의 게시글에 좋아요를 누르면 알림을 생성한다")
    void creates_like_notification_for_another_post() {
        given(notificationRepository
                .existsByTypeAndActorIdAndSourceTypeAndSourceId(
                        NotificationType.LIKE,
                        actor.getId(),
                        NotificationSourceType.POST,
                        post.getId()
                )
        ).willReturn(false);

        notificationService.createLikeNotification(post, actor);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());

        Notification savedNotification = captor.getValue();

        assertThat(savedNotification.getReceiver()).isEqualTo(postAuthor);
        assertThat(savedNotification.getActor()).isEqualTo(actor);
        assertThat(savedNotification.getType()).isEqualTo(NotificationType.LIKE);
    }

    // 이미 동일한 좋아요 알림 이력이 존재하면 새로 생성하지 않는다.
    @Test
    @DisplayName("동일한 좋아요 알림이 존재하면 다시 생성하지 않는다")
    void does_not_create_duplicate_like_notification() {
        // given : 이미 좋아요 알림이 주어진 상태
        given(notificationRepository
                .existsByTypeAndActorIdAndSourceTypeAndSourceId(
                        NotificationType.LIKE,
                        actor.getId(),
                        NotificationSourceType.POST,
                        post.getId()
                )
        ).willReturn(true);

        // when : 좋아요 알림 생성
        notificationService.createLikeNotification(post, actor);

        // then
        verify(notificationRepository)
                .existsByTypeAndActorIdAndSourceTypeAndSourceId(
                        NotificationType.LIKE,
                        actor.getId(),
                        NotificationSourceType.POST,
                        post.getId()
                );

        verify(notificationRepository, never())
                .save(any(Notification.class));
    }


}
