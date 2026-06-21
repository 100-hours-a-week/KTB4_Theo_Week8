package com.theo.community_api.auth.repository;

import com.theo.community_api.auth.domain.LoginSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<LoginSession, Long> {

    Optional<LoginSession> findBySessionId(String sessionId);

    void deleteBySessionId(String sessionId);
}