package com.theo.community_api.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LoginSession {
    private String sessionId;
    private Long userId;
    private LocalDateTime createdAt;
}