package com.woowacourse.pickgit.user.presentation;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForLoginAndGuestUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForOnlyLoginUser;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserForUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileImageEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileImageEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.presentation.dto.request.ContributionRequestDto;
import com.woowacourse.pickgit.user.presentation.dto.request.ProfileDescriptionRequest;
import com.woowacourse.pickgit.user.presentation.dto.response.ContributionResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.FollowResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.ProfileDescriptionResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.ProfileImageEditResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.UserProfileResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.UserSearchResponse;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@CrossOrigin(value = "*")
@RequestMapping("/api/profiles")
@RestController
public class UserController {

    private final UserService userService;

    @ForOnlyLoginUser
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getAuthenticatedUserProfile(
        @Authenticated AppUser appUser
    ) {
        AuthUserForUserRequestDto authUserRequestDto = AuthUserForUserRequestDto.from(appUser);
        UserProfileResponseDto responseDto = userService.getMyUserProfile(authUserRequestDto);

        return ResponseEntity.ok(createUserProfileResponse(responseDto));
    }

    @ForLoginAndGuestUser
    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
        @Authenticated AppUser appUser,
        @PathVariable String username
    ) {
        AuthUserForUserRequestDto authUserRequestDto = AuthUserForUserRequestDto.from(appUser);
        UserProfileResponseDto responseDto =
            userService.getUserProfile(authUserRequestDto, username);

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

    @ForOnlyLoginUser
    @PostMapping("/{username}/followings")
    public ResponseEntity<FollowResponse> followUser(
        @Authenticated AppUser appUser,
        @PathVariable String username,
        @RequestParam Boolean githubFollowing
    ) {
        FollowRequestDto followRequestDto = FollowRequestDto.builder()
            .authUserRequestDto(AuthUserForUserRequestDto.from(appUser))
            .targetName(username)
            .githubFollowing(githubFollowing)
            .build();

        FollowResponseDto followResponseDto =
            userService.followUser(followRequestDto);

        return ResponseEntity.ok(createFollowResponse(followResponseDto));
    }

    @ForOnlyLoginUser
    @DeleteMapping("/{username}/followings")
    public ResponseEntity<FollowResponse> unfollowUser(
        @Authenticated AppUser appUser,
        @PathVariable String username,
        @RequestParam Boolean githubUnfollowing
    ) {
        FollowRequestDto unfollowRequestDto = FollowRequestDto.builder()
            .authUserRequestDto(AuthUserForUserRequestDto.from(appUser))
            .targetName(username)
            .githubFollowing(githubUnfollowing)
            .build();

        FollowResponseDto followResponseDto =
            userService.unfollowUser(unfollowRequestDto);

        return ResponseEntity.ok(createFollowResponse(followResponseDto));
    }

    private FollowResponse createFollowResponse(FollowResponseDto followResponseDto) {
        return FollowResponse.builder()
            .followerCount(followResponseDto.getFollowerCount())
            .following(followResponseDto.isFollowing())
            .build();
    }

    @ForOnlyLoginUser
    @PutMapping("/me/image")
    public ResponseEntity<ProfileImageEditResponse> editProfileImage(
        @Authenticated AppUser appUser,
        @RequestBody byte[] image
    ) {
        AuthUserForUserRequestDto authUserRequestDto = AuthUserForUserRequestDto.from(appUser);
        ProfileImageEditRequestDto profileImageEditRequestDto = ProfileImageEditRequestDto
            .builder()
            .image(image)
            .build();
        ProfileImageEditResponseDto responseDto =
            userService.editProfileImage(authUserRequestDto, profileImageEditRequestDto);
        return ResponseEntity.ok(new ProfileImageEditResponse(responseDto.getImageUrl()));
    }

    @ForOnlyLoginUser
    @PutMapping("/me/description")
    public ResponseEntity<ProfileDescriptionResponse> editProfileDescription(
        @Authenticated AppUser appUser,
        @Valid @RequestBody ProfileDescriptionRequest request
    ) {
        AuthUserForUserRequestDto authUserRequestDto = AuthUserForUserRequestDto.from(appUser);
        String editResult = userService.editProfileDescription(
            authUserRequestDto,
            request.getDescription()
        );
        return ResponseEntity.ok(new ProfileDescriptionResponse(editResult));
    }

    @ForOnlyLoginUser
    @GetMapping("/{username}/contributions")
    public ResponseEntity<ContributionResponse> getContributions(
        @Authenticated AppUser user,
        @PathVariable String username
    ) {
        ContributionResponseDto responseDto =
            userService.calculateContributions(createContributionRequestDto(user, username));
        return ResponseEntity.ok(createContributionResponse(responseDto));
    }

    private ContributionRequestDto createContributionRequestDto(AppUser user, String username) {
        return ContributionRequestDto.builder()
            .accessToken(user.getAccessToken())
            .username(username)
            .build();
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

    @ForLoginAndGuestUser
    @GetMapping("/{username}/followings")
    public ResponseEntity<List<UserSearchResponse>> searchFollowings(
        @Authenticated AppUser appUser,
        @PathVariable String username,
        @RequestParam Long page,
        @RequestParam Long limit
    ) {
        AuthUserForUserRequestDto authUserRequestDto = AuthUserForUserRequestDto.from(appUser);
        FollowSearchRequestDto followSearchRequestDto = FollowSearchRequestDto.builder()
            .username(username)
            .page(page)
            .limit(limit)
            .build();
        List<UserSearchResponseDto> userSearchResponseDtos =
            userService.searchFollowings(authUserRequestDto, followSearchRequestDto);
        return ResponseEntity.ok(createUserSearchResponses(userSearchResponseDtos));
    }

    @ForLoginAndGuestUser
    @GetMapping("/{username}/followers")
    public ResponseEntity<List<UserSearchResponse>> searchFollowers(
        @Authenticated AppUser appUser,
        @PathVariable String username,
        @RequestParam Long page,
        @RequestParam Long limit
    ) {
        AuthUserForUserRequestDto authUserRequestDto = AuthUserForUserRequestDto.from(appUser);
        FollowSearchRequestDto followSearchRequestDto = FollowSearchRequestDto.builder()
            .username(username)
            .page(page)
            .limit(limit)
            .build();
        List<UserSearchResponseDto> userSearchResponseDtos =
            userService.searchFollowers(authUserRequestDto, followSearchRequestDto);
        return ResponseEntity.ok(createUserSearchResponses(userSearchResponseDtos));
    }

    private List<UserSearchResponse> createUserSearchResponses(
        List<UserSearchResponseDto> userSearchResponseDtos
    ) {
        return userSearchResponseDtos.stream()
            .map(this::createUserSearchResponse)
            .collect(toList());
    }

    private UserSearchResponse createUserSearchResponse(
        UserSearchResponseDto userSearchResponseDto
    ) {
        return UserSearchResponse.builder()
            .imageUrl(userSearchResponseDto.getImageUrl())
            .username(userSearchResponseDto.getUsername())
            .following(userSearchResponseDto.getFollowing())
            .build();
    }
}
