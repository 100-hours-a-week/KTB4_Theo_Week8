package com.theo.community_api.user.dto;

import com.theo.community_api.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private String email;
    private String nickname;
    private String profileImage;

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage()
        );
    }
}
