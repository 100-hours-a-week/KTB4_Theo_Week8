package com.theo.community_api.auth.controller;

import com.theo.community_api.auth.dto.IssuedTokens;
import com.theo.community_api.auth.dto.TokenResponse;
import com.theo.community_api.auth.service.AuthService;
import com.theo.community_api.auth.token.RefreshTokenCookieProvider;
import com.theo.community_api.common.ApiResponse;
import com.theo.community_api.user.dto.LoginRequest;
import com.theo.community_api.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request,
            @CookieValue(name = RefreshTokenCookieProvider.COOKIE_NAME, required = false) String currentRefreshToken
    ) {
        IssuedTokens tokens = userService.login(request, currentRefreshToken);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshTokenCookieProvider.create(tokens.refreshToken()).toString()
                )
                .body(ApiResponse.of("login_success", TokenResponse.from(tokens)));
    }

    // 토큰 재발급 (RTR 방식 : 리프레시 토큰과 액세스 토큰 둘 다 재발급)
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(
            @CookieValue(name = RefreshTokenCookieProvider.COOKIE_NAME, required = false) String refreshToken
    ) {
        IssuedTokens tokens = authService.reissue(refreshToken);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshTokenCookieProvider.create(tokens.refreshToken()).toString()
                )
                .body(ApiResponse.of("token_reissue_success", TokenResponse.from(tokens)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = RefreshTokenCookieProvider.COOKIE_NAME, required = false) String refreshToken
    ) {
        authService.logout(refreshToken);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshTokenCookieProvider.expire().toString()
                )
                .body(ApiResponse.of("logout_success"));
    }
}
