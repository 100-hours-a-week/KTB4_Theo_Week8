package com.theo.community_api.auth.scheduler;

import com.theo.community_api.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final AuthService authService;

    // 매일 새벽 3시에 만료된 Refresh Token 정리
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupExpiredRefreshTokens() {
        authService.deleteExpiredRefreshTokens();
    }
}
