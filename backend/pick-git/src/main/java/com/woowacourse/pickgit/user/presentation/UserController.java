package com.woowacourse.pickgit.user.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.presentation.dto.response.FollowResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.UserProfileResponse;
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
            userService.getMyUserProfile(new AuthUserRequestDto(user.getUsername()));

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
        return UserProfileResponse.builder()
            .name(userProfileResponseDto.getName())
            .imageUrl(userProfileResponseDto.getImageUrl())
            .description(userProfileResponseDto.getDescription())
            .followerCount(userProfileResponseDto.getFollowerCount())
            .followingCount(userProfileResponseDto.getFollowingCount())
            .postCount(userProfileResponseDto.getPostCount())
            .githubUrl(userProfileResponseDto.getGithubUrl())
            .company(userProfileResponseDto.getCompany())
            .location(userProfileResponseDto.getLocation())
            .website(userProfileResponseDto.getWebsite())
            .twitter(userProfileResponseDto.getTwitter())
            .following(userProfileResponseDto.getFollowing())
            .build();
    }

    @PostMapping("/{username}/followings")
    public ResponseEntity<FollowResponse> followUser(
        @Authenticated AppUser user,
        @PathVariable String username) {
        validateIsGuest(user);

        AuthUserRequestDto authUserRequestDto = new AuthUserRequestDto(user.getUsername());
        FollowResponseDto followResponseDto =
            userService.followUser(authUserRequestDto, username);

        return ResponseEntity.ok(createFollowResponse(followResponseDto));
    }

    @DeleteMapping("/{username}/followings")
    public ResponseEntity<FollowResponse> unfollowUser(
        @Authenticated AppUser user,
        @PathVariable String username) {
        validateIsGuest(user);

        AuthUserRequestDto authUserRequestDto = new AuthUserRequestDto(user.getUsername());
        FollowResponseDto followResponseDto =
            userService.unfollowUser(authUserRequestDto, username);

        return ResponseEntity.ok(createFollowResponse(followResponseDto));
    }

    private void validateIsGuest(AppUser user) {
        if (user.isGuest()) {
            throw new UnauthorizedException();
        }
    }

    private FollowResponse createFollowResponse(FollowResponseDto followResponseDto) {
        return FollowResponse.builder()
            .followerCount(followResponseDto.getFollowerCount())
            .following(followResponseDto.isFollowing())
            .build();
    }
}
