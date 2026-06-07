package com.theo.community_api.auth.service;

import com.theo.community_api.auth.domain.LoginSession;
import com.theo.community_api.auth.repository.SessionRepository;
import com.theo.community_api.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SessionRepository sessionRepository;

    // 세션ID 생성
    public String createSession(Long userId) {
        String sessionId = UUID.randomUUID().toString();
        LoginSession session = new LoginSession(sessionId, userId, LocalDateTime.now());
        sessionRepository.save(session);
        return sessionId;
    }

    // 세션 ID를 통해 userID 조회
    public Long getLoginUserId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new UnauthorizedException("unauthorized_request");
        }

        LoginSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new UnauthorizedException("unauthorized_request"));

        return session.getUserId();
    }

    public void logout(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new UnauthorizedException("unauthorized_request");
        }

        sessionRepository.deleteBySessionId(sessionId);
    }
}