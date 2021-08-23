package com.woowacourse.pickgit.acceptance.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.common.request_builder.PickGitRequest;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserForUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowRequestDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.presentation.dto.response.UserSearchResponse;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

class UserAcceptance_GetFollowingsAndFollowers extends AcceptanceTest {

    private static final String FOLLOWINGS_API_URL =
        "/api/profiles/{username}/followings?page={page}&limit={limit}";
    private static final String FOLLOWERS_API_URL =
        "/api/profiles/{username}/followers?page={page}&limit={limit}";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @DisplayName("로그인 - 특정 유저의 팔로잉 목록을 조회한다. (팔로잉 여부 true/false, 본인은 null)")
    @Test
    void searchFollowings_Login_FollowingVarious() {
        // given
        User target = UserFactory.user("target");
        List<User> usersInDb = new ArrayList<>(UserFactory.mockSearchUsers());
        usersInDb.add(UserFactory.user("testUser"));
        userRepository.save(target);
        userRepository.saveAll(usersInDb);

        AuthUserForUserRequestDto targetAuthDto =
            AuthUserForUserRequestDto.from(new LoginUser(target.getName(), "token"));
        for (User user : usersInDb) {
            FollowRequestDto requestDto = FollowRequestDto.builder()
                .authUserRequestDto(targetAuthDto)
                .targetName(user.getName())
                .githubFollowing(false)
                .build();
            userService.followUser(requestDto);
        }
        AuthUserForUserRequestDto testerAuthDto =
            AuthUserForUserRequestDto.from(new LoginUser("testUser", "token"));
        for (int i = 0; i < 3; i++) {
            FollowRequestDto requestDto = FollowRequestDto.builder()
                .authUserRequestDto(testerAuthDto)
                .targetName(usersInDb.get(i).getName())
                .githubFollowing(false)
                .build();
            userService.followUser(requestDto);
        }

        // when
        List<UserSearchResponse> response = PickGitRequest
            .get(FOLLOWINGS_API_URL, "target", "0", "10")
            .withUser("testUser")
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response)
            .extracting("username", "following")
            .containsExactly(
                tuple(usersInDb.get(0).getName(), true),
                tuple(usersInDb.get(1).getName(), true),
                tuple(usersInDb.get(2).getName(), true),
                tuple(usersInDb.get(3).getName(), false),
                tuple(usersInDb.get(4).getName(), false),
                tuple(usersInDb.get(5).getName(), null)
            ).hasSize(6);
    }

    @DisplayName("비로그인 - 특정 유저의 팔로잉 목록을 조회한다. (팔로잉 여부 모두 null)")
    @Test
    void searchFollowings_Guest_FollowingNull() {
        // given
        User target = UserFactory.user("target");
        List<User> usersInDb = UserFactory.mockSearchUsers();
        userRepository.save(target);
        userRepository.saveAll(usersInDb);

        AuthUserForUserRequestDto targetAuthDto =
            AuthUserForUserRequestDto.from(new LoginUser(target.getName(), "token"));
        for (User user : usersInDb) {
            FollowRequestDto requestDto = FollowRequestDto.builder()
                .authUserRequestDto(targetAuthDto)
                .targetName(user.getName())
                .githubFollowing(false)
                .build();
            userService.followUser(requestDto);
        }

        // when
        List<UserSearchResponse> response = PickGitRequest
            .get(FOLLOWINGS_API_URL, "target", "0",
                "10")
            .withGuest()
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response)
            .extracting("username", "following")
            .containsExactly(
                tuple(usersInDb.get(0).getName(), null),
                tuple(usersInDb.get(1).getName(), null),
                tuple(usersInDb.get(2).getName(), null),
                tuple(usersInDb.get(3).getName(), null),
                tuple(usersInDb.get(4).getName(), null)
            ).hasSize(5);
    }

    @DisplayName("로그인 - 특정 유저의 팔로워 목록을 조회한다. (팔로잉 여부 true/false, 본인은 null)")
    @Test
    void searchFollowers_Login_FollowingVarious() {
        // given
        User target = UserFactory.user("target");
        List<User> usersInDb = new ArrayList<>(UserFactory.mockSearchUsers());
        usersInDb.add(UserFactory.user("testUser"));
        userRepository.save(target);
        userRepository.saveAll(usersInDb);

        for (User user : usersInDb) {
            AuthUserForUserRequestDto mockUserAuthDto =
                AuthUserForUserRequestDto.from(new LoginUser(user.getName(), "token"));
            FollowRequestDto requestDto = FollowRequestDto.builder()
                .authUserRequestDto(mockUserAuthDto)
                .targetName(target.getName())
                .githubFollowing(false)
                .build();
            userService.followUser(requestDto);
        }
        AuthUserForUserRequestDto testerAuthDto =
            AuthUserForUserRequestDto.from(new LoginUser("testUser", "token"));
        for (int i = 0; i < 3; i++) {
            FollowRequestDto requestDto = FollowRequestDto.builder()
                .authUserRequestDto(testerAuthDto)
                .targetName(usersInDb.get(i).getName())
                .githubFollowing(false)
                .build();
            userService.followUser(requestDto);
        }

        // when
        List<UserSearchResponse> response = PickGitRequest
            .get(FOLLOWERS_API_URL, "target", "0", "10")
            .withUser("testUser")
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response)
            .extracting("username", "following")
            .containsExactly(
                tuple(usersInDb.get(0).getName(), true),
                tuple(usersInDb.get(1).getName(), true),
                tuple(usersInDb.get(2).getName(), true),
                tuple(usersInDb.get(3).getName(), false),
                tuple(usersInDb.get(4).getName(), false),
                tuple(usersInDb.get(5).getName(), null)
            ).hasSize(6);
    }

    @DisplayName("비로그인 - 특정 유저의 팔로워 목록을 조회한다. (팔로잉 여부 모두 null)")
    @Test
    void searchFollowers_Guest_FollowingNull() {
        // given
        User target = UserFactory.user("target");
        List<User> usersInDb = UserFactory.mockSearchUsers();
        userRepository.save(target);
        userRepository.saveAll(usersInDb);

        for (User user : usersInDb) {
            AuthUserForUserRequestDto mockUserAuthDto =
                AuthUserForUserRequestDto.from(new LoginUser(user.getName(), "token"));

            FollowRequestDto requestDto = FollowRequestDto.builder()
                .authUserRequestDto(mockUserAuthDto)
                .targetName(target.getName())
                .githubFollowing(false)
                .build();
            userService.followUser(requestDto);
        }

        // when
        List<UserSearchResponse> response = PickGitRequest
            .get(FOLLOWERS_API_URL, "target", "0", "10")
            .withGuest()
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response)
            .extracting("username", "following")
            .containsExactly(
                tuple(usersInDb.get(0).getName(), null),
                tuple(usersInDb.get(1).getName(), null),
                tuple(usersInDb.get(2).getName(), null),
                tuple(usersInDb.get(3).getName(), null),
                tuple(usersInDb.get(4).getName(), null)
            ).hasSize(5);
    }
}
