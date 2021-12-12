package com.woowacourse.pickgit.integration.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.integration.IntegrationTest;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserForUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import com.woowacourse.pickgit.user.domain.search.UserSearchEngine;
import com.woowacourse.pickgit.user.presentation.dto.UserAssembler;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

public class UserServiceIntegrationTest_Query extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSearchEngine userSearchEngine;

    @DisplayName("비로그인 유저는 내 프로필을 조회할 수 없다.")
    @Test
    void getMyUserProfile_Guest_Failure() {
        // given
        AuthUserForUserRequestDto requestDto = createGuestAuthUserRequestDto();

        // when, then
        assertThatCode(() -> userService.getMyUserProfile(requestDto))
            .isInstanceOf(InvalidUserException.class);
    }

    @DisplayName("로그인된 사용자는 자신의 프로필을 조회할 수 있다.")
    @Test
    void getMyUserProfile_WithMyName_Success() {
        //given
        User loginUser = userRepository.save(UserFactory.user());
        AuthUserForUserRequestDto requestDto = createLoginAuthUserRequestDto(loginUser.getName());
        UserProfileResponseDto responseDto = UserFactory.mockLoginUserProfileResponseDto();

        //when
        UserProfileResponseDto myUserProfile = userService.getMyUserProfile(requestDto);

        //then
        assertThat(myUserProfile)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("게스트 유저는 유저 이름으로 검색하여 다른 유저의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_FindByNameInCaseOfGuestUser_Success() {
        //given
        AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
        UserProfileResponseDto responseDto = UserFactory.mockGuestUserProfileResponseDto();
        User targetUser = userRepository.save(UserFactory.user());

        //when
        UserProfileResponseDto userProfile =
            userService.getUserProfile(authUserRequestDto, targetUser.getName());

        //then
        assertThat(userProfile.getName()).isEqualTo(responseDto.getName());
    }

    @DisplayName("게스트 유저는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_FindByInvalidNameInCaseOfGuestUser_400Exception() {
        // given
        AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();

        // when
        assertThatThrownBy(() ->
            userService.getUserProfile(authUserRequestDto, "invalidName")
        ).isInstanceOf(InvalidUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("유효하지 않은 유저입니다.");
    }

    @DisplayName("로그인 유저는 팔로잉한 유저 이름을 검색하여 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_FindByNameInCaseOfLoginUserIsFollowing_Success() {
        // given
        User loginUser = userRepository.save(UserFactory.user("testUser"));
        User target = userRepository.save(UserFactory.user("testUser2"));
        AuthUserForUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        FollowRequestDto requestDto = FollowRequestDto.builder()
            .authUserRequestDto(authUserRequestDto)
            .targetName(target.getName())
            .githubFollowing(false)
            .build();

        userService.followUser(requestDto);

        UserProfileResponseDto responseDto =
            UserFactory.mockLoginUserProfileIsFollowingResponseDto();

        // when
        UserProfileResponseDto userProfile =
            userService.getUserProfile(authUserRequestDto, target.getName());

        // then
        assertThat(userProfile)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("로그인 유저는 팔로잉하지 않은 유저 이름을 검색하여 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_FindByNameInCaseOfLoginUserIsNotFollowing_Success() {
        // given
        User loginUser = userRepository.save(UserFactory.user("testUser"));
        User target = userRepository.save(UserFactory.user("testUser2"));
        AuthUserForUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        UserProfileResponseDto responseDto =
            UserFactory.mockLoginUserProfileIsNotFollowingResponseDto();

        // when
        UserProfileResponseDto userProfile =
            userService.getUserProfile(authUserRequestDto, target.getName());

        // then
        assertThat(userProfile)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색한다. 단, 자기 자신은 검색되지 않는다. (팔로잉한 여부 boolean)")
    @Test
    void searchUser_LoginUser_Success() {
        // given
        String searchKeyword = "bing";
        List<User> usersInDb = UserFactory.mockSearchUsers();
        User loginUser = usersInDb.get(0);
        List<User> searchedUsers = usersInDb.subList(1, usersInDb.size());
        AuthUserForUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto(
            loginUser.getName());

        userSearchEngine.save(userRepository.save(loginUser));
        userSearchEngine.saveAll(userRepository.saveAll(searchedUsers));

        FollowRequestDto requestDto = FollowRequestDto.builder()
            .authUserRequestDto(authUserRequestDto)
            .targetName(searchedUsers.get(0).getName())
            .githubFollowing(false)
            .build();

        // when
        userService.followUser(requestDto);

        List<UserSearchResponseDto> searchResult =
            userService.searchUser(authUserRequestDto, searchKeyword, PageRequest.of(0, 10));

        // then
        assertThat(searchResult).hasSize(4);
        assertThat(searchResult)
            .extracting("username", "imageUrl", "following")
            .containsExactly(
                tuple(searchedUsers.get(0).getName(), searchedUsers.get(0).getImage(), true),
                tuple(searchedUsers.get(1).getName(), searchedUsers.get(1).getImage(), false),
                tuple(searchedUsers.get(2).getName(), searchedUsers.get(2).getImage(), false),
                tuple(searchedUsers.get(3).getName(), searchedUsers.get(3).getImage(), false)
            );
    }

    @DisplayName("비 로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색한다. (팔로잉 필드 null)")
    @Test
    void searchUser_GuestUser_Success() {
        // given
        String searchKeyword = "bing";
        AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
        List<User> userInDb = UserFactory.mockSearchUsers();
        userSearchEngine.saveAll(userRepository.saveAll(userInDb));

        // when
        List<UserSearchResponseDto> searchResult =
            userService.searchUser(authUserRequestDto, searchKeyword, PageRequest.of(0, 3));

        // then
        assertThat(searchResult)
            .extracting("username", "imageUrl", "following")
            .containsExactly(
                tuple(userInDb.get(0).getName(), userInDb.get(0).getImage(), null),
                tuple(userInDb.get(1).getName(), userInDb.get(1).getImage(), null),
                tuple(userInDb.get(2).getName(), userInDb.get(2).getImage(), null)
            );
    }

    @DisplayName("로그인 - 특정 유저의 팔로잉 목록을 조회한다. (팔로잉 필드는 true/false, 본인은 null)")
    @Test
    void searchFollowings_LoginUser_Success() {
        // given
        AuthUserForUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto("bingbing");
        List<User> usersInDb = UserFactory.mockSearchUsers();
        User targetUser = UserFactory.user("target");

        userRepository.saveAll(usersInDb);
        userRepository.save(targetUser);

        usersInDb.forEach(mockUser -> {
            AuthUserForUserRequestDto targetAuthDto = createLoginAuthUserRequestDto("target");
            FollowRequestDto requestDto = FollowRequestDto.builder()
                .authUserRequestDto(targetAuthDto)
                .targetName(mockUser.getName())
                .githubFollowing(false)
                .build();
            userService.followUser(requestDto);
        });

        for (int i = 0; i < 3; i++) {
            FollowRequestDto requestDto = FollowRequestDto.builder()
                .authUserRequestDto(authUserRequestDto)
                .targetName(usersInDb.get(i).getName())
                .githubFollowing(false)
                .build();
            userService.followUser(requestDto);
        }

        // when
        List<UserSearchResponseDto> response =
            userService.searchFollowings(authUserRequestDto, "target", PageRequest.of(0, 10));

        // then
        assertThat(response)
            .extracting("username", "following")
            .containsExactly(
                tuple(usersInDb.get(0).getName(), true),
                tuple(usersInDb.get(1).getName(), true),
                tuple(usersInDb.get(2).getName(), true),
                tuple(usersInDb.get(3).getName(), false),
                tuple(usersInDb.get(4).getName(), null)
            );
    }

    @DisplayName("비로그인 - 특정 유저의 팔로잉 목록을 조회한다. (팔로잉 필드는 모두 null)")
    @Test
    void searchFollowings_GuestUser_Success() {
        // given
        AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
        List<User> usersInDb = UserFactory.mockSearchUsers();
        User targetUser = UserFactory.user("target");

        userRepository.saveAll(usersInDb);
        userRepository.save(targetUser);

        usersInDb.forEach(mockUser -> {
            AuthUserForUserRequestDto targetAuthDto = createLoginAuthUserRequestDto("target");

            FollowRequestDto requestDto = FollowRequestDto.builder()
                .authUserRequestDto(targetAuthDto)
                .targetName(mockUser.getName())
                .githubFollowing(false)
                .build();
            userService.followUser(requestDto);
        });

        // when
        List<UserSearchResponseDto> response =
            userService.searchFollowings(authUserRequestDto, "target", PageRequest.of(0, 10));

        // then
        assertThat(response)
            .extracting("username", "following")
            .containsExactly(
                tuple(usersInDb.get(0).getName(), null),
                tuple(usersInDb.get(1).getName(), null),
                tuple(usersInDb.get(2).getName(), null),
                tuple(usersInDb.get(3).getName(), null),
                tuple(usersInDb.get(4).getName(), null)
            );
    }

    @DisplayName("로그인 - 특정 유저의 팔로워 목록을 조회한다. (팔로잉 필드는 true/false, 본인은 null)")
    @Test
    void searchFollowers_LoginUser_Success() {
        // given
        AuthUserForUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto("bingbing");
        List<User> usersInDb = UserFactory.mockSearchUsers();
        User targetUser = UserFactory.user("target");

        userRepository.saveAll(usersInDb);
        userRepository.save(targetUser);

        usersInDb.forEach(mockUser -> {
            AuthUserForUserRequestDto mockUserAuthDto =
                createLoginAuthUserRequestDto(mockUser.getName());

            FollowRequestDto requestDto = FollowRequestDto.builder()
                .authUserRequestDto(mockUserAuthDto)
                .targetName("target")
                .githubFollowing(false)
                .build();
            userService.followUser(requestDto);
        });

        for (int i = 0; i < 3; i++) {
            FollowRequestDto requestDto = FollowRequestDto.builder()
                .authUserRequestDto(authUserRequestDto)
                .targetName(usersInDb.get(i).getName())
                .githubFollowing(false)
                .build();
            userService.followUser(requestDto);
        }

        // when
        List<UserSearchResponseDto> response =
            userService.searchFollowers(authUserRequestDto, "target", PageRequest.of(0, 10));

        // then
        assertThat(response)
            .extracting("username", "following")
            .containsExactly(
                tuple(usersInDb.get(0).getName(), true),
                tuple(usersInDb.get(1).getName(), true),
                tuple(usersInDb.get(2).getName(), true),
                tuple(usersInDb.get(3).getName(), false),
                tuple(usersInDb.get(4).getName(), null)
            );
    }

    @DisplayName("비로그인 - 특정 유저의 팔로워 목록을 조회한다. (팔로잉 필드는 모두 null)")
    @Test
    void searchFollowers_GuestUser_Success() {
        // given
        AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
        List<User> usersInDb = UserFactory.mockSearchUsers();
        User targetUser = UserFactory.user("target");

        userRepository.saveAll(usersInDb);
        userRepository.save(targetUser);

        usersInDb.forEach(mockUser -> {
            AuthUserForUserRequestDto mockUserAuthDto = createLoginAuthUserRequestDto(
                mockUser.getName());

            FollowRequestDto requestDto = FollowRequestDto.builder()
                .authUserRequestDto(mockUserAuthDto)
                .targetName("target")
                .githubFollowing(false)
                .build();
            userService.followUser(requestDto);
        });

        // when
        List<UserSearchResponseDto> response =
            userService.searchFollowers(authUserRequestDto, "target", PageRequest.of(0, 10));

        // then
        assertThat(response)
            .extracting("username", "following")
            .containsExactly(
                tuple(usersInDb.get(0).getName(), null),
                tuple(usersInDb.get(1).getName(), null),
                tuple(usersInDb.get(2).getName(), null),
                tuple(usersInDb.get(3).getName(), null),
                tuple(usersInDb.get(4).getName(), null)
            );
    }

    private AuthUserForUserRequestDto createLoginAuthUserRequestDto(String username) {
        AppUser appUser = new LoginUser(username, "Bearer testToken");
        return UserAssembler.authUserForUserRequestDto(appUser);
    }

    private AuthUserForUserRequestDto createGuestAuthUserRequestDto() {
        return UserAssembler.authUserForUserRequestDto(new GuestUser());
    }
}
