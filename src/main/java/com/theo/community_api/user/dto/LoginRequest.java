package com.theo.community_api.user.dto;

import com.theo.community_api.common.ValidationConst;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "invalid_request")
    @Email(message = "invalid_email_format")
    private String email;

    @NotBlank(message = "invalid_request")
    @Pattern(
            regexp = ValidationConst.PASSWORD_REGEX,
            message = "invalid_password_format"
    )
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
