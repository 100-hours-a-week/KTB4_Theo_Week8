package com.theo.community_api.user.domain;

import lombok.Getter;

@Getter
public class User {
    private Long userId;

    private String email;
    private String password;
    private String nickname;
    private String profileImage;

    private boolean isDeleted;

    public User(Long userId, String email, String password, String nickname, String profileImage) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.isDeleted = false;
    }

    // 닉네임과 프로필 이미지 값 변경
    public void updateProfile(String nickname, String profileImage){
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    // 비밀번호 수정
    public void updatePassword(String password){
        this.password = password;
    }

    // 회원 탈퇴 시 활용 (서비스)
    public void delete(){
        this.email = "deleted_user_" + this.userId + "@deleted.local";
        this.password = null;
        this.nickname = "알 수 없음";
        this.profileImage = null;
        this.isDeleted = true;
    }
}