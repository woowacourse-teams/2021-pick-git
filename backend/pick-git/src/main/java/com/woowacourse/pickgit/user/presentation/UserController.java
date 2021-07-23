package com.woowacourse.pickgit.user.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.AuthUserResponseDto;
import com.woowacourse.pickgit.user.application.dto.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.UserProfileResponseDto;
import com.woowacourse.pickgit.user.presentation.dto.FollowResponse;
import com.woowacourse.pickgit.user.presentation.dto.UserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(value = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getAuthenticatedUserProfile(
        @Authenticated AppUser user) {
        validateIsGuest(user);

        UserProfileResponseDto responseDto =
            userService.getMyUserProfile(new AuthUserResponseDto(user.getUsername()));

        return ResponseEntity.ok(createUserProfileResponse(responseDto));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
        @Authenticated AppUser appUser,
        @PathVariable String username) {
        UserProfileResponseDto responseDto = userService.getUserProfile(appUser, username);

        return ResponseEntity.ok(createUserProfileResponse(responseDto));
    }

    private UserProfileResponse createUserProfileResponse(
        UserProfileResponseDto userProfileResponseDto) {
        return new UserProfileResponse(
            userProfileResponseDto.getName(),
            userProfileResponseDto.getImage(),
            userProfileResponseDto.getDescription(),
            userProfileResponseDto.getFollowerCount(),
            userProfileResponseDto.getFollowingCount(),
            userProfileResponseDto.getPostCount(),
            userProfileResponseDto.getGithubUrl(),
            userProfileResponseDto.getCompany(),
            userProfileResponseDto.getLocation(),
            userProfileResponseDto.getWebsite(),
            userProfileResponseDto.getTwitter(),
            userProfileResponseDto.getFollowing());
    }

    @PostMapping("/{username}/followings")
    public ResponseEntity<FollowResponse> followUser(
        @Authenticated AppUser user,
        @PathVariable String username) {
        validateIsGuest(user);

        AuthUserResponseDto authUserResponseDto = new AuthUserResponseDto(user.getUsername());
        FollowResponseDto followResponseDto =
            userService.followUser(authUserResponseDto, username);

        return ResponseEntity.ok(createFollowResponse(followResponseDto));
    }

    @DeleteMapping("/{username}/followings")
    public ResponseEntity<FollowResponse> unfollowUser(
        @Authenticated AppUser user,
        @PathVariable String username) {
        validateIsGuest(user);

        AuthUserResponseDto authUserResponseDto = new AuthUserResponseDto(user.getUsername());
        FollowResponseDto followResponseDto =
            userService.unfollowUser(authUserResponseDto, username);

        return ResponseEntity.ok(createFollowResponse(followResponseDto));
    }

    private void validateIsGuest(AppUser user) {
        if (user.isGuest()) {
            throw new UnauthorizedException();
        }
    }

    private FollowResponse createFollowResponse(FollowResponseDto followResponseDto) {
        return new FollowResponse(
            followResponseDto.getFollowerCount(),
            followResponseDto.isFollowing());
    }
}
