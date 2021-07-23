package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.user.application.dto.UserProfileResponseDto;
import com.woowacourse.pickgit.user.domain.User;

public class UserFactory {

    private UserFactory() {
    }

    public static User user(String name) {
        return createUser(null, name);
    }

    public static User user(Long id, String name) {
        return createUser(id, name);
    }

    public static User user() {
        return createUser(null,"yjksw");
    }

    public static User createUser(Long id, String name) {
        return MockUser.builder()
            .id(id)
            .name(name)
            .build();
    }

    public static UserProfileResponseDto mockLoginUserProfileServiceDto() {
        return UserProfileResponseDto.builder()
            .name("yjksw")
            .image("http://img.com")
            .description("The Best")
            .followerCount(0)
            .followingCount(11)
            .postCount(1)
            .githubUrl("https://github.com/yjksw")
            .company("woowacourse")
            .location("Seoul")
            .website("www.pick-git.com")
            .twitter("pick-git twitter")
            .following(false)
            .build();
    }

    public static UserProfileResponseDto mockUnLoginUserProfileServiceDto() {
        return UserProfileResponseDto.builder()
            .name("yjksw")
            .image("http://img.com")
            .description("The Best")
            .followerCount(0)
            .followingCount(11)
            .postCount(1)
            .githubUrl("https://github.com/yjksw")
            .company("woowacourse")
            .location("Seoul")
            .website("www.pick-git.com")
            .twitter("pick-git twitter")
            .following(null)
            .build();
    }
}
