package com.theo.community_api.auth.repository;

import com.theo.community_api.auth.domain.LoginSession;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class SessionRepository {

    private final Map<String, LoginSession> sessionStore = new HashMap<>();

    public LoginSession save(LoginSession session) {
        sessionStore.put(session.getSessionId(), session);
        return session;
    }

    public Optional<LoginSession> findBySessionId(String sessionId) {
        return Optional.ofNullable(sessionStore.get(sessionId));
    }

    public void deleteBySessionId(String sessionId) {
        sessionStore.remove(sessionId);
    }
}