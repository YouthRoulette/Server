package com.youthroulette.server.user;

import com.youthroulette.server.user.dto.ProfileResponse;
import com.youthroulette.server.user.dto.UpdateNicknameRequest;
import com.youthroulette.server.user.dto.UpdateProfileRequest;
import com.youthroulette.server.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse me() {
        return userService.me();
    }

    @PatchMapping("/nickname")
    public UserResponse updateNickname(@Valid @RequestBody UpdateNicknameRequest request) {
        return userService.updateNickname(request);
    }

    @PatchMapping("/profile")
    public ProfileResponse updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(request);
    }
}
