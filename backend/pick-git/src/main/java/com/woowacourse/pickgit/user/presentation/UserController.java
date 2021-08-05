package com.woowacourse.pickgit.user.presentation;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.presentation.dto.request.ContributionRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.presentation.dto.request.ProfileEditRequest;
import com.woowacourse.pickgit.user.presentation.dto.response.ContributionResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.FollowResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.ProfileEditResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.UserProfileResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.UserSearchResponse;
import java.util.List;
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
        AuthUserRequestDto authUserRequestDto = AuthUserRequestDto.from(appUser);
        UserProfileResponseDto responseDto = userService.getMyUserProfile(authUserRequestDto);

        return ResponseEntity.ok(createUserProfileResponse(responseDto));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
        @Authenticated AppUser appUser,
        @PathVariable String username
    ) {
        AuthUserRequestDto authUserRequestDto = AuthUserRequestDto.from(appUser);
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

    @PostMapping("/{username}/followings")
    public ResponseEntity<FollowResponse> followUser(
        @Authenticated AppUser appUser,
        @PathVariable String username
    ) {
        AuthUserRequestDto authUserRequestDto = AuthUserRequestDto.from(appUser);
        FollowResponseDto followResponseDto =
            userService.followUser(authUserRequestDto, username);

        return ResponseEntity.ok(createFollowResponse(followResponseDto));
    }

    @DeleteMapping("/{username}/followings")
    public ResponseEntity<FollowResponse> unfollowUser(
        @Authenticated AppUser appUser,
        @PathVariable String username
    ) {
        AuthUserRequestDto authUserRequestDto = AuthUserRequestDto.from(appUser);
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

    @PostMapping("/me")
    public ResponseEntity<ProfileEditResponse> editProfile(
        @Authenticated AppUser appUser,
        @ModelAttribute ProfileEditRequest request
    ) {
        AuthUserRequestDto authUserRequestDto = AuthUserRequestDto.from(appUser);
        ProfileEditRequestDto profileEditRequestDto = ProfileEditRequestDto
            .builder()
            .image(request.getImage())
            .decription(request.getDescription())
            .build();
        ProfileEditResponseDto responseDto =
            userService.editProfile(authUserRequestDto, profileEditRequestDto);

        return ResponseEntity.ok(
            new ProfileEditResponse(
                responseDto.getImageUrl(),
                responseDto.getDescription()
            )
        );
    }

    @GetMapping("/{username}/contributions")
    public ResponseEntity<ContributionResponse> getContributions(
        @Authenticated AppUser user,
        @PathVariable String username
    ) {
        ContributionResponseDto responseDto = userService
            .calculateContributions(createContributionRequestDto(user, username));

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

    @GetMapping("/{username}/followings")
    public ResponseEntity<List<UserSearchResponse>> searchFollowings(
        @Authenticated AppUser appUser,
        @PathVariable String username,
        @RequestParam Long page,
        @RequestParam Long limit
    ) {
        AuthUserRequestDto authUserRequestDto = AuthUserRequestDto.from(appUser);
        FollowSearchRequestDto followSearchRequestDto = FollowSearchRequestDto.builder()
            .username(username)
            .page(page)
            .limit(limit)
            .build();
        List<UserSearchResponseDto> userSearchResponseDtos =
            userService.searchFollowings(authUserRequestDto, followSearchRequestDto);
        return ResponseEntity.ok(createUserSearchResponses(userSearchResponseDtos));
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<List<UserSearchResponse>> searchFollowers(
        @Authenticated AppUser appUser,
        @PathVariable String username,
        @RequestParam Long page,
        @RequestParam Long limit
    ) {
        AuthUserRequestDto authUserRequestDto = AuthUserRequestDto.from(appUser);
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
