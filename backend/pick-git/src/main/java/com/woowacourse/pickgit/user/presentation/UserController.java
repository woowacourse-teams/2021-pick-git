package com.woowacourse.pickgit.user.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.AuthUserServiceDto;
import com.woowacourse.pickgit.user.application.dto.FollowServiceDto;
import com.woowacourse.pickgit.user.application.dto.UserProfileServiceDto;
import com.woowacourse.pickgit.user.presentation.dto.FollowResponseDto;
import com.woowacourse.pickgit.user.presentation.dto.UserProfileResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
        @Authenticated AppUser appUser) {
        UserProfileServiceDto userProfileServiceDto = userService.getAuthUserProfile(
            new AuthUserServiceDto(appUser.getUsername())
        );

        return ResponseEntity.ok(getUserProfileResponseDto(userProfileServiceDto));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@PathVariable String username) {
        UserProfileServiceDto userProfileServiceDto = userService.getUserProfile(username);

        return ResponseEntity.ok(getUserProfileResponseDto(userProfileServiceDto));
    }

    @PostMapping("/{username}/followings")
    public ResponseEntity<FollowResponseDto> followUser(
        @Authenticated AppUser appUser,
        @PathVariable String username
    ) {
        AuthUserServiceDto authUserServiceDto =
            new AuthUserServiceDto(appUser.getUsername());

        FollowServiceDto followServiceDto = userService.followUser(authUserServiceDto, username);

        return ResponseEntity.ok(createFollowResponseDto(followServiceDto));
    }

    @DeleteMapping("/{username}/followings")
    public ResponseEntity<FollowResponseDto> unfollowUser(
        @Authenticated AppUser appUser,
        @PathVariable String username
    ) {
        AuthUserServiceDto authUserServiceDto =
            new AuthUserServiceDto(appUser.getUsername());

        FollowServiceDto followServiceDto = userService.unfollowUser(authUserServiceDto, username);

        return ResponseEntity.ok(createFollowResponseDto(followServiceDto));
    }

    private UserProfileResponseDto getUserProfileResponseDto(
        UserProfileServiceDto userProfileServiceDto) {
        return new UserProfileResponseDto(
            userProfileServiceDto.getName(), userProfileServiceDto.getImage(),
            userProfileServiceDto.getDescription(), userProfileServiceDto.getFollowerCount(),
            userProfileServiceDto.getFollowingCount(), userProfileServiceDto.getPostCount(),
            userProfileServiceDto.getGithubUrl(), userProfileServiceDto.getCompany(),
            userProfileServiceDto.getLocation(), userProfileServiceDto.getWebsite(),
            userProfileServiceDto.getTwitter()
        );
    }

    private FollowResponseDto createFollowResponseDto(FollowServiceDto followServiceDto) {
        return new FollowResponseDto(
            followServiceDto.getFollowerCount(),
            followServiceDto.isFollowing());
    }
}
