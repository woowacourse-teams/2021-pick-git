package com.woowacourse.pickgit.user.presentation.dto;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserForUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileImageEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.UserSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.presentation.dto.request.ContributionRequestDto;
import com.woowacourse.pickgit.user.presentation.dto.response.ContributionResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.FollowResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.ProfileDescriptionResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.UserProfileResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.UserSearchResponse;
import java.util.List;

public class UserAssembler {

    public static AuthUserForUserRequestDto authUserForUserRequestDto(AppUser appUser) {
        if (appUser.isGuest()) {
            return AuthUserForUserRequestDto.builder()
                .isGuest(true)
                .build();
        }

        return AuthUserForUserRequestDto.builder()
            .username(appUser.getUsername())
            .accessToken(appUser.getAccessToken())
            .isGuest(false)
            .build();
    }

    public static UserSearchRequestDto userSearchRequestDto(
        String keyword,
        Long page,
        Long limit
    ) {
        return UserSearchRequestDto.builder()
            .keyword(keyword)
            .page(page)
            .limit(limit)
            .build();
    }

    public static List<UserSearchResponse> userSearchResponses(
        List<UserSearchResponseDto> userSearchResponseDtos
    ) {
        return userSearchResponseDtos.stream()
            .map(UserAssembler::userSearchResponse)
            .collect(toList());
    }

    public static UserSearchResponse userSearchResponse(
        UserSearchResponseDto userSearchResponseDto
    ) {
        return UserSearchResponse.builder()
            .imageUrl(userSearchResponseDto.getImageUrl())
            .username(userSearchResponseDto.getUsername())
            .following(userSearchResponseDto.getFollowing())
            .build();
    }

    public static UserProfileResponse userProfileResponse(
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

    public static FollowRequestDto followRequestDto(
        AppUser appUser,
        String username,
        boolean githubFollowing
    ) {
        return FollowRequestDto.builder()
            .authUserRequestDto(UserAssembler.authUserForUserRequestDto(appUser))
            .targetName(username)
            .githubFollowing(githubFollowing)
            .build();
    }

    public static FollowResponse followResponse(FollowResponseDto followResponseDto) {
        return FollowResponse.builder()
            .followerCount(followResponseDto.getFollowerCount())
            .following(followResponseDto.isFollowing())
            .build();
    }

    public static ProfileImageEditRequestDto profileImageEditRequestDto(byte[] image) {
        return ProfileImageEditRequestDto
            .builder()
            .image(image)
            .build();
    }

    public static ProfileDescriptionResponse profileDescriptionResponse(String editResult) {
        return ProfileDescriptionResponse.builder()
            .description(editResult)
            .build();
    }

    public static ContributionRequestDto contributionRequestDto(AppUser user, String username) {
        return ContributionRequestDto.builder()
            .accessToken(user.getAccessToken())
            .username(username)
            .build();
    }

    public static ContributionResponse contributionResponse(ContributionResponseDto responseDto) {
        return ContributionResponse.builder()
            .starsCount(responseDto.getStarsCount())
            .commitsCount(responseDto.getCommitsCount())
            .prsCount(responseDto.getPrsCount())
            .issuesCount(responseDto.getIssuesCount())
            .reposCount(responseDto.getReposCount())
            .build();
    }

    public static FollowSearchRequestDto followSearchRequestDto(
        String username,
        Long page,
        Long limit
    ) {
        return FollowSearchRequestDto.builder()
            .username(username)
            .page(page)
            .limit(limit)
            .build();
    }

    private static UserSearchResponse createUserSearchResponse(
        UserSearchResponseDto userSearchResponseDto
    ) {
        return UserSearchResponse.builder()
            .imageUrl(userSearchResponseDto.getImageUrl())
            .username(userSearchResponseDto.getUsername())
            .following(userSearchResponseDto.getFollowing())
            .build();
    }
}
