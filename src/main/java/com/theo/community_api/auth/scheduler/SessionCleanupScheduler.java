package com.theo.community_api.auth.scheduler;

import com.theo.community_api.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionCleanupScheduler {

    private final AuthService authService;

    // 매일 새벽 3시에 만료 세션 전체 삭제
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupExpiredSessions() {
        int deletedCount = authService.deleteExpiredSessions();

        log.info("만료 세션 정리 완료 - 삭제된 세션 수: {}", deletedCount);
    }
}