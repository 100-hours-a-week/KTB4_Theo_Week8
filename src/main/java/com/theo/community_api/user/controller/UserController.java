package com.theo.community_api.user.controller;

import com.theo.community_api.auth.service.AuthService;
import com.theo.community_api.common.ApiResponse;
import com.theo.community_api.user.dto.*;
import com.theo.community_api.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    // 회원 정보 가져오기
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId
    ){
        Long loginUserId = authService.getLoginUserId(sessionId);

        UserResponse response = userService.getUser(loginUserId);

        return ResponseEntity
                .ok(ApiResponse.of("get_user_success", response));
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        Long userId = userService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("signup_success", userId));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
            @Valid @RequestBody LoginRequest request,
            @CookieValue(name = "JSESSIONID", required = false) String currentSessionId
    ) {
        String sessionId = userService.login(request, currentSessionId);

        ResponseCookie cookie = ResponseCookie.from("JSESSIONID",sessionId)
                .httpOnly(true)
                .path("/")
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.of("login_success"));
    }

    // 회원정보 수정
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserUpdateResponse>> updateUser(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        Long loginUserId = authService.getLoginUserId(sessionId);
        UserUpdateResponse response = userService.updateUser(loginUserId, request);

        return ResponseEntity
                .ok(ApiResponse.of("user_update_success", response));
    }

    // 비밀번호 수정
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId,
            @Valid @RequestBody PasswordUpdateRequest request
    ) {
        Long loginUserId = authService.getLoginUserId(sessionId);
        userService.updatePassword(loginUserId, request);



        return ResponseEntity
                .ok(ApiResponse.of("password_update_success"));
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId
    ) {
        Long loginUserId = authService.getLoginUserId(sessionId);
        userService.deleteUser(loginUserId);
        authService.logout(sessionId);
        ResponseCookie cookie =
                ResponseCookie.from(
                                "JSESSIONID",
                                ""
                        )
                        .httpOnly(true)
                        .path("/")
                        .maxAge(0)
                        .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.of("user_delete_success"));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(value = "JSESSIONID", required = false) String sessionId
    ) {
        authService.logout(sessionId);

        ResponseCookie cookie = ResponseCookie.from("JSESSIONID","")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.of("logout_success"));
    }
}