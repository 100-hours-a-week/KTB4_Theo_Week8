package com.theo.community_api.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theo.community_api.common.ApiResponse;
import com.theo.community_api.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor    // 엑세스 토큰 문제 있는 경우 처리
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        ErrorCode errorCode =
                (ErrorCode) request.getAttribute(
                        "AUTH_ERROR_CODE"
                );

        if (errorCode == null) {
            errorCode =
                    ErrorCode.ACCESS_TOKEN_REQUIRED;
        }

        response.setStatus(
                errorCode.getStatus().value()
        );

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        objectMapper.writeValue(
                response.getWriter(),
                ApiResponse.of(
                        errorCode.getMessage()
                )
        );
    }
}
