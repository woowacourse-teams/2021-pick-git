package com.woowacourse.pickgit.user.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.presentation.dto.request.ProfileEditRequest;
import com.woowacourse.pickgit.user.presentation.dto.response.ContributionResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.FollowResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.ProfileEditResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.UserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        @Authenticated AppUser appUser
    ) {
        validateIsGuest(appUser);

        AuthUserRequestDto authUserRequestDto =
            new AuthUserRequestDto(appUser.getUsername2(), appUser.isGuest());
        UserProfileResponseDto responseDto = userService.getMyUserProfile(authUserRequestDto);

        return ResponseEntity.ok(createUserProfileResponse(responseDto));
    }

    private void validateIsGuest(AppUser appUser) {
        if (appUser.isGuest()) {
            throw new UnauthorizedException();
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
        @Authenticated AppUser appUser,
        @PathVariable String username
    ) {
        AuthUserRequestDto authUserRequestDto =
            new AuthUserRequestDto(appUser.getUsername2(), appUser.isGuest());
        UserProfileResponseDto responseDto = userService
            .getUserProfile(authUserRequestDto, username);

        return ResponseEntity.ok(createUserProfileResponse(responseDto));
    }

    private UserProfileResponse createUserProfileResponse(
        UserProfileResponseDto userProfileResponseDto
    ) {
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

    @GetMapping("/{username}/contributions")
    public ResponseEntity<ContributionResponse> getContributions(@PathVariable String username) {
        ContributionResponseDto responseDto = userService.calculateContributions(username);

        return ResponseEntity.ok(createContributionResponse(responseDto));
    }

    private ContributionResponse createContributionResponse(ContributionResponseDto responseDto) {
        return ContributionResponse.builder()
            .starsCount(responseDto.getStarsCount())
            .commitsCount(responseDto.getCommitsCount())
            .prsCount(responseDto.getPrsCount())
            .issuesCount(responseDto.getIssuesCount())
            .reposCount(responseDto.getReposCount())
            .build();
    }

    @PostMapping("/{username}/followings")
    public ResponseEntity<FollowResponse> followUser(
        @Authenticated AppUser appUser,
        @PathVariable String username
    ) {
        validateIsGuest(appUser);

        AuthUserRequestDto authUserRequestDto =
            new AuthUserRequestDto(appUser.getUsername2(), appUser.isGuest());
        FollowResponseDto followResponseDto =
            userService.followUser(authUserRequestDto, username);

        return ResponseEntity.ok(createFollowResponse(followResponseDto));
    }

    @PostMapping("/me")
    public ResponseEntity<ProfileEditResponse> editProfile(
        @Authenticated AppUser appUser,
        @ModelAttribute ProfileEditRequest request
    ) {
        ProfileEditRequestDto requestDto = ProfileEditRequestDto
            .builder()
            .image(request.getImage())
            .decription(request.getDescription())
            .build();
        ProfileEditResponseDto responseDto = userService.editProfile(appUser, requestDto);

        return ResponseEntity.ok(
            new ProfileEditResponse(
                responseDto.getImageUrl(),
                responseDto.getDescription()
            )
        );
    }

    @DeleteMapping("/{username}/followings")
    public ResponseEntity<FollowResponse> unfollowUser(
        @Authenticated AppUser appUser,
        @PathVariable String username
    ) {
        validateIsGuest(appUser);

        AuthUserRequestDto authUserRequestDto =
            new AuthUserRequestDto(appUser.getUsername2(), appUser.isGuest());
        FollowResponseDto followResponseDto =
            userService.unfollowUser(authUserRequestDto, username);

        return ResponseEntity.ok(createFollowResponse(followResponseDto));
    }

    private FollowResponse createFollowResponse(FollowResponseDto followResponseDto) {
        return FollowResponse.builder()
            .followerCount(followResponseDto.getFollowerCount())
            .following(followResponseDto.isFollowing())
            .build();
    }
}
