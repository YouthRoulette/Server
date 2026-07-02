package com.youthroulette.server.security;

import com.youthroulette.server.common.ApiException;
import com.youthroulette.server.user.User;
import com.youthroulette.server.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUser {
    private final UserRepository userRepository;

    public AuthUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }
        return userRepository.findById(principal.getId())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "인증 사용자를 찾을 수 없습니다."));
    }
}
