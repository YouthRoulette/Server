package com.youthroulette.server.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


//인증 안 된 상태로 보호된 API 호출 시 여기로 옴.
//SecurityConfig에서 formLogin/httpBasic을 꺼놨기 때문에, 이걸 등록 안 하면
//Spring Security 기본 동작(403 Forbidden, 빈 바디 또는 Spring 기본 에러 포맷)이 나가서 명세서 JSON 포맷({status, message, timestamp})이 안 지켜짐.

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String body = """
                {"status":401,"message":"로그인이 필요합니다.","timestamp":"%s"}
                """.formatted(LocalDateTime.now());

        response.getWriter().write(body);
    }
}
