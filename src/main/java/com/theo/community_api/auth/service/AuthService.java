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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    // 세션ID 생성
    @Transactional
    public String createSession(Long userId, String currentSessionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 현재 요청에 기존 쿠키가 있는 경우
        if (currentSessionId != null && !currentSessionId.isBlank()) {
            Optional<LoginSession> optionalSession =
                    sessionRepository.findBySessionId(currentSessionId);

            if (optionalSession.isPresent()) {
                LoginSession session = optionalSession.get();

                boolean sameUser = session.getUser().getId().equals(userId);
                boolean notExpired = !session.isExpired();

                // 같은 브라우저의 기존 세션이고, 같은 유저고, 아직 만료되지 않았다면 재사용
                if (sameUser && notExpired) {
                    return session.getSessionId();
                }
            }
        }

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

    @Transactional
    public int deleteExpiredSessions() { // 만료된 세션 전체 삭제
        return sessionRepository.deleteExpiredSessions(LocalDateTime.now());
    }
}