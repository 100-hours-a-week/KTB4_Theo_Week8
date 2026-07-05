package com.theo.community_api.user;

import com.theo.community_api.common.exception.BusinessException;
import com.theo.community_api.common.exception.ErrorCode;
import com.theo.community_api.user.domain.User;
import com.theo.community_api.user.dto.*;
import com.theo.community_api.user.repository.UserRepository;
import com.theo.community_api.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입에 성공하면 저장된 회원 ID를 반환한다")
    void signup_success() {
        // given
        SignupRequest request = new SignupRequest(
                "theo1234@gmail.com",
                "Password123!",
                "Password123!",
                "theo",
                null
        );

        User savedUser = mock(User.class);
        given(savedUser.getId()).willReturn(1L);

        given(userRepository.save(any(User.class)))
                .willReturn(savedUser);

        // when
        Long userId = userService.signup(request);

        // then
        assertThat(userId).isEqualTo(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 다르면 회원가입에 실패한다")
    void signup_fail_when_password_mismatch() {
        // given
        SignupRequest request = new SignupRequest(
                "theo1234@gmail.com",
                "Password123!",
                "Different123!",
                "theo",
                null
        );



        // when : 회원가입 시도, then : 비밀번호 불일치 에러 발생!
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.signup(request) // when
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.PASSWORD_MISMATCH);
    }

    @Test
    @DisplayName("이메일이 중복되면 회원가입에 실패한다")
    void signup_fail_when_email_duplicate() {
        // given
        SignupRequest request = new SignupRequest(
                "duplicate@gmail.com",
                "Password123!",
                "Password123!",
                "theo",
                null
        );

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.signup(request)
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.EMAIL_ALREADY_EXIST);
    }

    @Test
    @DisplayName("닉네임이 중복되면 회원가입에 실패한다")
    void signup_fail_when_nickname_duplicate() {
        // given
        SignupRequest request = new SignupRequest(
                "theo1234@gmail.com",
                "Password123!",
                "Password123!",
                "duplicateNickname",
                null
        );

        given(userRepository.existsByEmail(request.getEmail()))
                .willReturn(false);

        given(userRepository.existsByNickname(request.getNickname()))
                .willReturn(true);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.signup(request)
        );

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.NICKNAME_ALREADY_EXIST);

        verify(userRepository, never()) // 회원가입 함수 호출되어서는 안된다.
                .save(any(User.class));

        verifyNoInteractions(passwordEncoder); // 비밀번호 암호화 실행되서는 안된다.
    }

    @Test
    @DisplayName("회원가입 시 비밀번호는 암호화되어 저장된다")
    void signup_encrypts_password() {
        // given
        SignupRequest request = new SignupRequest(
                "theo1234@gmail.com",
                "Password123!",
                "Password123!",
                "theo",
                null
        );

        String encodedPassword = "encoded-password";

        given(passwordEncoder.encode(request.getPassword()))
                .willReturn(encodedPassword);

        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        userService.signup(request);

        // then
        verify(passwordEncoder).encode(request.getPassword());

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getPassword())
                .isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("존재하는 회원을 조회하면 회원 정보를 반환한다")
    void getUser_success() {
        // given
        Long userId = 1L;

        User user = new User(
                "theo1234@gmail.com",
                "encoded-password",
                "theo",
                "profile.png"
        );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        // when
        UserResponse response = userService.getUser(userId);

        // then
        assertThat(response.getEmail())
                .isEqualTo("theo1234@gmail.com");

        assertThat(response.getNickname())
                .isEqualTo("theo");

        assertThat(response.getProfileImage())
                .isEqualTo("profile.png");
    }

    @Test
    @DisplayName("탈퇴한 회원을 조회하면 USER_NOT_FOUND가 발생한다")
    void getUser_fail_when_user_deleted() {
        // given
        Long userId = 1L;

        User deletedUser = new User(
                "theo1234@gmail.com",
                "encoded-password",
                "theo",
                null
        );

        deletedUser.delete();

        given(userRepository.findById(userId))
                .willReturn(Optional.of(deletedUser));

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.getUser(userId)
        );

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("회원정보를 정상적으로 수정한다")
    void updateUser_success() {
        // given
        Long userId = 1L;

        User user = new User(
                "theo1234@gmail.com",
                "encoded-password",
                "theo",
                "old-profile.png"
        );

        UserUpdateRequest request = new UserUpdateRequest(
                "newTheo",
                "new-profile.png"
        );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(userRepository.existsByNickname(request.getNickname()))
                .willReturn(false);

        // when
        UserUpdateResponse response =
                userService.updateUser(userId, request);

        // then
        assertThat(response.getNickname())
                .isEqualTo("newTheo");

        assertThat(response.getProfileImage())
                .isEqualTo("new-profile.png");
    }

    @Test
    @DisplayName("현재 닉네임과 같으면 SAME_NICKNAME이 발생한다")
    void updateUser_fail_when_same_nickname() {
        // given
        Long userId = 1L;

        User user = new User(
                "theo1234@gmail.com",
                "encoded-password",
                "theo",
                null
        );

        UserUpdateRequest request = new UserUpdateRequest(
                "theo",
                "new-profile.png"
        );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.updateUser(userId, request)
        );

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.SAME_NICKNAME);
    }

    @Test
    @DisplayName("다른 회원이 사용하는 닉네임이면 NICKNAME_ALREADY_EXIST가 발생한다")
    void updateUser_fail_when_nickname_duplicate() {
        // given
        Long userId = 1L;

        User user = new User(
                "theo1234@gmail.com",
                "encoded-password",
                "theo",
                null
        );

        UserUpdateRequest request = new UserUpdateRequest(
                "existingNickname",
                "new-profile.png"
        );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(userRepository.existsByNickname(request.getNickname()))
                .willReturn(true);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.updateUser(userId, request)
        );

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.NICKNAME_ALREADY_EXIST);
    }

    @Test
    @DisplayName("비밀번호를 암호화하여 변경한다")
    void updatePassword_success() {
        // given
        Long userId = 1L;

        User user = new User(
                "theo1234@gmail.com",
                "encoded-old-password",
                "theo",
                null
        );

        PasswordUpdateRequest request =
                new PasswordUpdateRequest(
                        "NewPassword123!",
                        "NewPassword123!"
                );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )).willReturn(false);

        given(passwordEncoder.encode(request.getPassword()))
                .willReturn("encoded-new-password");

        // when
        userService.updatePassword(userId, request);

        // then
        assertThat(user.getPassword())
                .isEqualTo("encoded-new-password");
    }

    @Test
    @DisplayName("비밀번호 확인이 다르면 PASSWORD_MISMATCH가 발생한다")
    void updatePassword_fail_when_password_mismatch() {
        // given
        Long userId = 1L;

        User user = new User(
                "theo1234@gmail.com",
                "encoded-old-password",
                "theo",
                null
        );

        PasswordUpdateRequest request =
                new PasswordUpdateRequest(
                        "NewPassword123!",
                        "DifferentPassword123!"
                );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.updatePassword(userId, request)
        );

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.PASSWORD_MISMATCH);
    }

    @Test
    @DisplayName("기존 비밀번호와 같으면 SAME_PASSWORD가 발생한다")
    void updatePassword_fail_when_same_password() {
        // given
        Long userId = 1L;

        User user = new User(
                "theo1234@gmail.com",
                "encoded-old-password",
                "theo",
                null
        );

        PasswordUpdateRequest request =
                new PasswordUpdateRequest(
                        "OldPassword123!",
                        "OldPassword123!"
                );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )).willReturn(true);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.updatePassword(userId, request)
        );

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.SAME_PASSWORD);
    }
}

