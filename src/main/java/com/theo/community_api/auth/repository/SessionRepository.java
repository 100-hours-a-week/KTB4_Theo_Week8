package com.theo.community_api.auth.repository;

import com.theo.community_api.auth.domain.LoginSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<LoginSession, Long> {

    Optional<LoginSession> findBySessionId(String sessionId);

    // 특정 유저의 세션 삭제 (로그아웃)
    void deleteBySessionId(String sessionId);

    @Modifying
    @Query("""
        delete from LoginSession s
        where s.expiredAt <= :now
    """)
    int deleteExpiredSessions(LocalDateTime now); // 전체 만료 세션 삭제 메서드 (주기적)
    // 로그로 확인 시 삭제된 행 개수를 받기 위해서 int로 처리
}