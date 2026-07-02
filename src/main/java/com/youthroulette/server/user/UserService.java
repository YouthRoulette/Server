package com.youthroulette.server.user;

import com.youthroulette.server.security.AuthUser;
import com.youthroulette.server.user.dto.ProfileResponse;
import com.youthroulette.server.user.dto.UpdateNicknameRequest;
import com.youthroulette.server.user.dto.UpdateProfileRequest;
import com.youthroulette.server.user.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final AuthUser authUser;

    public UserService(AuthUser authUser) {
        this.authUser = authUser;
    }

    @Transactional(readOnly = true)
    public UserResponse me() {
        return UserResponse.from(authUser.get());
    }

    @Transactional
    public UserResponse updateNickname(UpdateNicknameRequest request) {
        User user = authUser.get();
        user.updateNickname(request.nickname());
        return UserResponse.from(user);
    }

    @Transactional
    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = authUser.get();
        user.updateProfile(request.emojiIndex(), request.colorIndex());
        return ProfileResponse.from(user);
    }
}
