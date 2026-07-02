package com.youthroulette.server.security;

import com.youthroulette.server.common.ApiException;
import com.youthroulette.server.common.ErrorCode;
import com.youthroulette.server.user.User;
import com.youthroulette.server.user.UserRepository;
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
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        return userRepository.findById(principal.getId())
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }
}
