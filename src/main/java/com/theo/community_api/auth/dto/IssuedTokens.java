package com.theo.community_api.auth.dto;

public record IssuedTokens(
        String accessToken,
        String refreshToken,
        String tokenType,
        long accessTokenExpiresIn
) {
}