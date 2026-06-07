package com.theo.community_api.user.service;

import com.theo.community_api.auth.service.AuthService;
import com.theo.community_api.common.exception.BadRequestException;
import com.theo.community_api.common.exception.ConflictException;
import com.theo.community_api.common.exception.NotFoundException;
import com.theo.community_api.common.exception.UnauthorizedException;
import com.theo.community_api.user.domain.User;
import com.theo.community_api.user.dto.*;
import com.theo.community_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthService authService;

    // 회원가입
    public Long signup(SignupRequest request) {
        // 비밀번호, 재입력 비밀번호가 같은지 확인
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new BadRequestException("password_mismatch");
        }

        // 이메일 중복확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("email_already_exist");
        }

        // 닉네임 중복확인
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ConflictException("nickname_already_exist");
        }

        // 사용자 추가
        User user = userRepository.save(
                request.getEmail(),
                request.getPassword(),
                request.getNickname(),
                request.getProfileImage());

        return user.getUserId();
    }

    // 로그인
    public String login(LoginRequest request) {
        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("invalid_credentials"));

        // 비밀번호와 유저 비밀번호가 같은지 확인
        if (!user.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("invalid_credentials");
        }

        // 탈퇴한 회원은 재로그인 불가
        if(user.isDeleted()){
            throw new UnauthorizedException("invalid_credentials");
        }
        return authService.createSession(user.getUserId()); // 세션 ID 반환
    }

    // 회원정보 수정
    public UserUpdateResponse updateUser(Long loginUserId, UserUpdateRequest request) {
        // 유저 존재여부 확인
        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new NotFoundException("user_not_found"));

        // 삭제된 유저가 요청 시
        if(user.isDeleted()){
            throw new UnauthorizedException("unauthorized_request");
        }

        // 닉네임 중복여부 확인
        if (userRepository.existsByNickname(request.getNickname())
                && user.getNickname().equals(request.getNickname())) {
            throw new ConflictException("nickname_already_exist");
        }

        // 회원정보 갱신
        user.updateProfile(request.getNickname(), request.getProfileImage());

        return new UserUpdateResponse(user.getNickname(), user.getProfileImage());
    }

    // 비밀번호 수정
    public void updatePassword(Long loginUserId, PasswordUpdateRequest request) {

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new NotFoundException("user_not_found"));

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new BadRequestException("password_mismatch");
        }

        user.updatePassword(request.getPassword());
    }

    // 회원 탈퇴
    public void deleteUser(Long loginUserId) {
        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new NotFoundException("user_not_found"));

        // 이미 삭제된 유저라면
        if(user.isDeleted()){
            throw new NotFoundException("user_not_found");
        }

        user.delete();
    }
}