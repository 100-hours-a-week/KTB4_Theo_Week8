package com.theo.community_api.auth.security;

import com.theo.community_api.auth.jwt.JwtTokenProvider;
import com.theo.community_api.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal( // 액세스 토큰이 유효한지 여부 확인
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            authenticate(request);
        } catch (ExpiredJwtException exception) {
            SecurityContextHolder.clearContext();

            request.setAttribute(
                    "AUTH_ERROR_CODE",
                    ErrorCode.ACCESS_TOKEN_EXPIRED
            );
        } catch (JwtException | IllegalArgumentException | AuthenticationException exception
        ) {
            SecurityContextHolder.clearContext();

            request.setAttribute(
                    "AUTH_ERROR_CODE",
                    ErrorCode.INVALID_ACCESS_TOKEN
            );
        }

        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request) { // 필터 인증하기
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        String token = resolveAccessToken(request);

        if (token == null) {
            return;
        }

        Claims claims = jwtTokenProvider.parseClaims(token);

        if (!jwtTokenProvider.isAccessToken(claims)) {
            return;
        }

        Long userId = jwtTokenProvider.getUserId(claims);
        CustomUserDetails userDetails = customUserDetailsService.loadUserById(userId);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader == null
                || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }
}
