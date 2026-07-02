package com.youthroulette.server.security;

import com.youthroulette.server.common.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
        throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
        char quote = '"';
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{" + quote + "code" + quote + ":" + quote + errorCode.name() + quote
            + "," + quote + "message" + quote + ":" + quote + errorCode.getMessage() + quote
            + "," + quote + "status" + quote + ":" + errorCode.getStatus().value() + "}");
    }
}
