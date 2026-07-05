package com.theo.community_api.auth.repository;

import com.theo.community_api.auth.domain.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) // 동시에 여러 리프레시 토큰 재발급 시 락 필요
    Optional<RefreshToken> findByTokenHash(String tokenHash); // 해시값으로 토큰 조회

    void deleteByTokenHash(String tokenHash); // 토큰 해시값으로 조회해서 제거

    void deleteAllByUserId(Long userId); // 유저ID로 접근해 리프레시 토큰 전부 삭제

    void deleteAllByExpiresAtBefore(LocalDateTime now); // 만료된 토큰 전부 제거 (스케줄러로 관리)
}
