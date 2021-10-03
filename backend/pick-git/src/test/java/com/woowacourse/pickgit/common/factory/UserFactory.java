package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.presentation.dto.request.ContributionRequestDto;
import java.util.List;

public class UserFactory {

    private UserFactory() {
    }

    public static User user(String name) {
        return createUser(null, name);
    }

    public static User user(Long id, String name) {
        return createUser(id, name);
    }

    public static User user(String name, String imageUrl) {
        return createUser(null, name, imageUrl);
    }

    public static User user(Long id, String name, String imageUrl) {
        return createUser(id, name, imageUrl);
    }


    public static User user() {
        return createUser(null, "testUser");
    }

    public static User createUser(Long id, String name) {
        return MockUser.builder()
            .id(id)
            .name(name)
            .build();
    }

    public static User createUser(Long id, String name, String imageUrl) {
        return MockUser.builder()
            .id(id)
            .name(name)
            .image(imageUrl)
            .build();
    }

    public static UserProfileResponseDto mockLoginUserProfileResponseDto() {
        return UserProfileResponseDto.builder()
            .name("testUser")
            .imageUrl("https://github.com/testImage.jpg")
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

    public static UserProfileResponseDto mockLoginUserProfileIsFollowingResponseDto() {
        return UserProfileResponseDto.builder()
            .name("testUser2")
            .imageUrl("https://github.com/testImage.jpg")
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
            .imageUrl("https://github.com/testImage.jpg")
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
            .imageUrl("https://github.com/testImage.jpg")
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

    public static ContributionRequestDto mockContributionRequestDto() {
        return ContributionRequestDto.builder()
            .accessToken("oauth.access.token")
            .username("testUser")
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

    public static List<User> mockSearchUsers() {
        User user1 = user("binghe");
        User user2 = user("bing");
        User user3 = user("jinbinghe");
        User user4 = user("bbbbinghe");
        User user5 = user("bingbing");

        return List.of(
            user1, user2, user3, user4, user5
        );
    }

    public static List<User> mockSearchUsersWithId() {
        User user1 = user(1L, "binghe");
        User user2 = user(2L,"bing");
        User user3 = user(3L,"jinbinghe");
        User user4 = user(4L,"bbbbinghe");
        User user5 = user(5L,"bingbing");

        return List.of(
            user1, user2, user3, user4, user5
        );
    }

    public static List<User> mockLikeUsersIncludingAuthor() {
        User user1 = user("user1");
        User user2 = user("user2");
        User user3 = user("user3");
        User user4 = user("user4");
        User user5 = user("user5");
        User author = user("author");

        return List.of(
            user1, user2, user3, user4, user5, author
        );
    }

    public static List<User> mockLikeUsers() {
        User user1 = user("user1", "https://github.com/testImage.jpg");
        User user2 = user("user2", "https://github.com/testImage.jpg");
        User user3 = user("user3", "https://github.com/testImage.jpg");
        User user4 = user("user4", "https://github.com/testImage.jpg");
        User user5 = user("user5", "https://github.com/testImage.jpg");

        return List.of(
            user1, user2, user3, user4, user5
        );
    }

    public static List<User> mockLikeUsersWithId() {
        User user1 = user(1L, "user1", "https://github.com/testImage.jpg");
        User user2 = user(2L, "user2", "https://github.com/testImage.jpg");
        User user3 = user(3L, "user3", "https://github.com/testImage.jpg");
        User user4 = user(4L, "user4", "https://github.com/testImage.jpg");
        User user5 = user(5L, "user5", "https://github.com/testImage.jpg");

        return List.of(
            user1, user2, user3, user4, user5
        );
    }
}
