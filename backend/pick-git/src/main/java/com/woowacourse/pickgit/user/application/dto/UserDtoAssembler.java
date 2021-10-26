package com.woowacourse.pickgit.user.application.dto;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.contribution.Contribution;
import java.util.List;
import java.util.function.Predicate;

public class UserDtoAssembler {

    private UserDtoAssembler() {
    }

    public static UserProfileResponseDto userProfileResponseDto(User user, Boolean following) {
        return UserProfileResponseDto.builder()
            .name(user.getName())
            .imageUrl(user.getImage())
            .description(user.getDescription())
            .followerCount(user.getFollowerCount())
            .followingCount(user.getFollowingCount())
            .postCount(user.getPostCount())
            .githubUrl(user.getGithubUrl())
            .company(user.getCompany())
            .location(user.getLocation())
            .website(user.getWebsite())
            .twitter(user.getTwitter())
            .following(following)
            .build();
    }

    public static List<UserSearchResponseDto> userSearchResponseDto(
        User loginUser,
        List<User> followings
    ) {
        return followings.stream()
            .map(followUser -> convert(loginUser, followUser))
            .collect(toList());
    }

    private static UserSearchResponseDto convert(User loginUser, User followUser) {
        if (loginUser.equals(followUser)) {
            return new UserSearchResponseDto(loginUser.getImage(), loginUser.getName(), null);
        }
        return new UserSearchResponseDto(
            followUser.getImage(),
            followUser.getName(),
            loginUser.isFollowing(followUser)
        );
    }

    public static List<UserSearchResponseDto> userSearchResponseDto(
        List<User> users
    ) {
        return users.stream()
            .map(user -> new UserSearchResponseDto(user.getImage(), user.getName(), null))
            .collect(toList());
    }

    public static List<UserSearchResponseDto> UserSearchResponseDto(
        User loginUser,
        List<User> users
    ) {
        return users
            .stream()
            .filter(isLoginUser(loginUser))
            .map(user -> new UserSearchResponseDto(
                user.getImage(),
                user.getName(),
                loginUser.isFollowing(user)))
            .collect(toList());
    }

    private static Predicate<User> isLoginUser(User loginUser) {
        return user -> !user.equals(loginUser);
    }

    public static FollowResponseDto followResponseDto(User target, boolean isFollowing) {
        return FollowResponseDto.builder()
            .followerCount(target.getFollowerCount())
            .isFollowing(isFollowing)
            .build();
    }

    public static ContributionResponseDto contributionResponseDto(Contribution contribution) {
        return ContributionResponseDto.builder()
            .starsCount(contribution.getStarsCount())
            .commitsCount(contribution.getCommitsCount())
            .prsCount(contribution.getPrsCount())
            .issuesCount(contribution.getIssuesCount())
            .reposCount(contribution.getReposCount())
            .build();
    }

}
