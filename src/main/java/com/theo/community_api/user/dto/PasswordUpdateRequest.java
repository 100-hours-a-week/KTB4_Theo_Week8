package com.theo.community_api.user.dto;

import com.theo.community_api.common.ValidationConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordUpdateRequest {
    @NotBlank(message = "blank_password")
    @Pattern(
            regexp = ValidationConst.PASSWORD_REGEX,
            message = "invalid_password_format"
    )
    private String password;

    @NotBlank(message = "blank_password")
    private String passwordConfirm;

    public PasswordUpdateRequest(String password, String passwordConfirm) {
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }
}
