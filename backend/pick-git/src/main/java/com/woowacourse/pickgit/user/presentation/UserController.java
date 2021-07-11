package com.woowacourse.pickgit.user.presentation;

import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.AuthUserServiceDto;
import com.woowacourse.pickgit.user.presentation.dto.AuthUserRequestDto;
import com.woowacourse.pickgit.user.presentation.dto.UserProfileResponseDto;
import com.woowacourse.pickgit.user.presentation.resolver.Authenticated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getAuthenticatedUserProfile(
        @Authenticated AuthUserRequestDto authUserRequestDto) {
        AuthUserServiceDto authUserServiceDto = new AuthUserServiceDto(
            authUserRequestDto.getGithubName());

        UserProfileResponseDto userProfileResponseDto =
            new UserProfileResponseDto(userService.getAuthUserProfile(authUserServiceDto));

        return ResponseEntity.ok(userProfileResponseDto);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@PathVariable String username) {
        UserProfileResponseDto userProfileResponseDto =
            new UserProfileResponseDto(userService.getUserProfile(username));

        return ResponseEntity.ok(userProfileResponseDto);
    }
}
