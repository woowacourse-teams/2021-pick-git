package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.domain.User;

public class UserFactory {

    private UserFactory() {
    }

    public static User user(Long id, String name) {
        return createUser(id, name);
    }

    public static User user(String name) {
        return createUser(null, name);
    }

    public static User user() {
        return createUser(null,"testUser");
    }

    public static User createUser(Long id, String name) {
        return MockUser.builder()
            .id(id)
            .name(name)
            .build();
    }

    public static UserProfileResponseDto mockLoginUserProfileResponseDto() {
        return UserProfileResponseDto.builder()
            .name("testUser")
            .imageUrl("http://img.com")
            .description("The Best")
            .followerCount(0)
            .followingCount(0)
            .postCount(0)
            .githubUrl("https://github.com/yjksw")
            .company("woowacourse")
            .location("Seoul")
            .website("www.pick-git.com")
            .twitter("pick-git twitter")
            .following(false)
            .build();
    }

    public static UserProfileResponseDto mockLoginUserProfileIsFollowingResponseDto() {
        return UserProfileResponseDto.builder()
            .name("testUser2")
            .imageUrl("http://img.com")
            .description("The Best")
            .followerCount(1)
            .followingCount(0)
            .postCount(0)
            .githubUrl("https://github.com/yjksw")
            .company("woowacourse")
            .location("Seoul")
            .website("www.pick-git.com")
            .twitter("pick-git twitter")
            .following(true)
            .build();
    }

    public static UserProfileResponseDto mockLoginUserProfileIsNotFollowingResponseDto() {
        return UserProfileResponseDto.builder()
            .name("testUser2")
            .imageUrl("http://img.com")
            .description("The Best")
            .followerCount(0)
            .followingCount(0)
            .postCount(0)
            .githubUrl("https://github.com/yjksw")
            .company("woowacourse")
            .location("Seoul")
            .website("www.pick-git.com")
            .twitter("pick-git twitter")
            .following(false)
            .build();
    }

    public static UserProfileResponseDto mockGuestUserProfileResponseDto() {
        return UserProfileResponseDto.builder()
            .name("testUser")
            .imageUrl("http://img.com")
            .description("The Best")
            .followerCount(0)
            .followingCount(0)
            .postCount(0)
            .githubUrl("https://github.com/yjksw")
            .company("woowacourse")
            .location("Seoul")
            .website("www.pick-git.com")
            .twitter("pick-git twitter")
            .following(null)
            .build();
    }

    public static ContributionResponseDto mockContributionResponseDto() {
        return ContributionResponseDto.builder()
            .starsCount(11)
            .commitsCount(48)
            .prsCount(48)
            .issuesCount(48)
            .reposCount(48)
            .build();
    }
}
