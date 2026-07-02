package com.youthroulette.server.auth;

import com.youthroulette.server.auth.dto.LoginRequest;
import com.youthroulette.server.auth.dto.LoginResponse;
import com.youthroulette.server.auth.dto.SignupRequest;
import com.youthroulette.server.common.ApiException;
import com.youthroulette.server.common.ErrorCode;
import com.youthroulette.server.security.JwtTokenProvider;
import com.youthroulette.server.user.User;
import com.youthroulette.server.user.UserRepository;
import com.youthroulette.server.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new ApiException(ErrorCode.LOGINID_DUPLICATED);
        }
        User user = new User(request.loginId(), passwordEncoder.encode(request.password()), request.nickname());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.loginId())
            .orElseThrow(() -> new ApiException(ErrorCode.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_CREDENTIALS);
        }
        return new LoginResponse(jwtTokenProvider.createAccessToken(user.getId(), user.getLoginId()), "Bearer");
    }
}
