package com.theo.community_api.notification.repository;

import com.theo.community_api.notification.domain.Notification;
import com.theo.community_api.notification.domain.NotificationSourceType;
import com.theo.community_api.notification.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    boolean existsByTypeAndActorIdAndSourceTypeAndSourceId(
            NotificationType type,
            Long actorId,
            NotificationSourceType sourceType,
            Long sourceId
    );
}
