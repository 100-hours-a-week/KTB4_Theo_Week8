package com.theo.community_api.auth.service;

import com.theo.community_api.auth.domain.LoginSession;
import com.theo.community_api.auth.repository.SessionRepository;
import com.theo.community_api.common.exception.BusinessException;
import com.theo.community_api.common.exception.ErrorCode;
import com.theo.community_api.user.domain.User;
import com.theo.community_api.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    // 세션ID 생성
    @Transactional
    public String createSession(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String sessionId = UUID.randomUUID().toString();

        LoginSession session = new LoginSession(sessionId, user, LocalDateTime.now().plusDays(7));
        sessionRepository.save(session);
        return sessionId;
    }

    // 세션 ID를 통해 userID 조회
    public Long getLoginUserId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        LoginSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST));

        if(session.isExpired()){
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        return session.getUser().getId();
    }

    @Transactional
    public void logout(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        sessionRepository.deleteBySessionId(sessionId);
    }
}