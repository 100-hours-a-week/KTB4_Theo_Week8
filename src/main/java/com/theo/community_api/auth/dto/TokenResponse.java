package com.theo.community_api.auth.dto;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long accessTokenExpiresIn
) {
    public static TokenResponse from(IssuedTokens tokens) {
        return new TokenResponse(
                tokens.accessToken(),
                tokens.tokenType(),
                tokens.accessTokenExpiresIn()
        );
    }
}
