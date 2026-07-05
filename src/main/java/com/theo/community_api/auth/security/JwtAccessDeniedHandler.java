package com.theo.community_api.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theo.community_api.common.ApiResponse;
import com.theo.community_api.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler { // ADMIN 권한부족 처리

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception
    ) throws IOException {

        ErrorCode errorCode =
                ErrorCode.ACCESS_DENIED;

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
