package com.woowacourse.pickgit.user.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForLoginAndGuestUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForOnlyLoginUser;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserForUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileImageEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileImageEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.presentation.dto.UserAssembler;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
        AuthUserForUserRequestDto authUserRequestDto =
            UserAssembler.authUserForUserRequestDto(appUser);

        UserProfileResponseDto responseDto = userService.getMyUserProfile(authUserRequestDto);

        return ResponseEntity.ok(UserAssembler.userProfileResponse(responseDto));
    }

    @ForLoginAndGuestUser
    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
        @Authenticated AppUser appUser,
        @PathVariable String username
    ) {
        AuthUserForUserRequestDto authUserRequestDto =
            UserAssembler.authUserForUserRequestDto(appUser);

        UserProfileResponseDto responseDto =
            userService.getUserProfile(authUserRequestDto, username);

        return ResponseEntity.ok(UserAssembler.userProfileResponse(responseDto));
    }

    @ForOnlyLoginUser
    @PostMapping("/{username}/followings")
    public ResponseEntity<FollowResponse> followUser(
        @Authenticated AppUser appUser,
        @PathVariable String username,
        @RequestParam Boolean githubFollowing
    ) {
        FollowRequestDto followRequestDto =
            UserAssembler.followRequestDto(appUser, username, githubFollowing);

        FollowResponseDto followResponseDto = userService.followUser(followRequestDto);

        FollowResponse followResponse = UserAssembler.followResponse(followResponseDto);

        return ResponseEntity.ok(followResponse);
    }

    @ForOnlyLoginUser
    @DeleteMapping("/{username}/followings")
    public ResponseEntity<FollowResponse> unfollowUser(
        @Authenticated AppUser appUser,
        @PathVariable String username,
        @RequestParam Boolean githubUnfollowing
    ) {
        FollowRequestDto unfollowRequestDto =
            UserAssembler.followRequestDto(appUser, username, githubUnfollowing);

        FollowResponseDto followResponseDto =
            userService.unfollowUser(unfollowRequestDto);

        return ResponseEntity.ok(UserAssembler.followResponse(followResponseDto));
    }

    @ForOnlyLoginUser
    @PutMapping("/me/image")
    public ResponseEntity<ProfileImageEditResponse> editProfileImage(
        @Authenticated AppUser appUser,
        @RequestBody byte[] image
    ) {
        AuthUserForUserRequestDto authUserRequestDto =
            UserAssembler.authUserForUserRequestDto(appUser);

        ProfileImageEditRequestDto profileImageEditRequestDto =
            UserAssembler.profileImageEditRequestDto(image);

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
        AuthUserForUserRequestDto authUserRequestDto =
            UserAssembler.authUserForUserRequestDto(appUser);

        String editResult =
            userService.editProfileDescription(authUserRequestDto, request.getDescription());

        return ResponseEntity.ok(UserAssembler.profileDescriptionResponse(editResult));
    }

    @ForOnlyLoginUser
    @GetMapping("/{username}/contributions")
    public ResponseEntity<ContributionResponse> getContributions(
        @Authenticated AppUser user,
        @PathVariable String username
    ) {
        ContributionRequestDto contributionRequestDto =
            UserAssembler.contributionRequestDto(user, username);

        ContributionResponseDto contributionResponseDto =
            userService.calculateContributions(contributionRequestDto);

        ContributionResponse contributionResponse = UserAssembler
            .contributionResponse(contributionResponseDto);

        return ResponseEntity.ok(contributionResponse);
    }

    @ForLoginAndGuestUser
    @GetMapping("/{username}/followings")
    public ResponseEntity<List<UserSearchResponse>> searchFollowings(
        @Authenticated AppUser appUser,
        @PathVariable String username,
        @PageableDefault Pageable pageable
    ) {
        AuthUserForUserRequestDto authUserRequestDto =
            UserAssembler.authUserForUserRequestDto(appUser);

        List<UserSearchResponseDto> userSearchResponseDtos =
            userService.searchFollowings(authUserRequestDto, username, pageable);

        List<UserSearchResponse> userSearchResponses = UserAssembler
            .userSearchResponses(userSearchResponseDtos);

        return ResponseEntity.ok(userSearchResponses);
    }

    @ForLoginAndGuestUser
    @GetMapping("/{username}/followers")
    public ResponseEntity<List<UserSearchResponse>> searchFollowers(
        @Authenticated AppUser appUser,
        @PathVariable String username,
        @PageableDefault Pageable pageable
    ) {
        AuthUserForUserRequestDto authUserRequestDto =
            UserAssembler.authUserForUserRequestDto(appUser);

        List<UserSearchResponseDto> userSearchResponseDtos =
            userService.searchFollowers(authUserRequestDto, username, pageable);

        List<UserSearchResponse> userSearchResponses =
            UserAssembler.userSearchResponses(userSearchResponseDtos);

        return ResponseEntity.ok(userSearchResponses);
    }
}
