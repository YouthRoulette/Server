package com.youthroulette.server.user;

import com.youthroulette.server.security.AuthUser;
import com.youthroulette.server.user.dto.UpdateUserRequest;
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
    public UserResponse updateMe(UpdateUserRequest request) {
        User user = authUser.get();
        user.updateNickname(request.nickname());
        return UserResponse.from(user);
    }
}
