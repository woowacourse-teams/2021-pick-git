package com.woowacourse.pickgit.integration.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.exception.user.DuplicateFollowException;
import com.woowacourse.pickgit.exception.user.InvalidFollowException;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@Import(InfrastructureTestConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("사용자는 자신의 프로필을 조회할 수 있다.")
    @Test
    void getMyUserProfile_WithMyName_Success() {
        //given
        AuthUserRequestDto requestDto = new AuthUserRequestDto("testUser");
        UserProfileResponseDto responseDto = UserFactory.mockLoginUserProfileResponseDto();

        userRepository.save(UserFactory.user());

        //when
        UserProfileResponseDto myUserProfile = userService.getMyUserProfile(requestDto);

        //then
        assertThat(myUserProfile)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("게스트는 유저 이름으로 검색하여 유저의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_FindByNameInCaseOfGuestUser_Success() {
        //given
        AppUser guestUser = new GuestUser();
        UserProfileResponseDto responseDto = UserFactory.mockGuestUserProfileResponseDto();

        userRepository.save(UserFactory.user());

        //when
        UserProfileResponseDto userProfile = userService.getUserProfile(guestUser, "testUser");

        //then
        assertThat(userProfile)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("사용자는 유저 이름으로 검색하여 유저의 프로필을 조회할 수 있다. - 팔로잉을 한 경우")
    @Test
    void getUserProfile_FindByNameInCaseOfLoginUserIsFollowing_Success() {
        // given
        AppUser loginUser = new LoginUser("testUser", "Bearer testToken");

        User source = userRepository.save(UserFactory.user("testUser"));
        User target = userRepository.save(UserFactory.user("testUser2"));

        AuthUserRequestDto requestDto = new AuthUserRequestDto(source.getName());
        userService.followUser(requestDto, target.getName());

        UserProfileResponseDto responseDto =
            UserFactory.mockLoginUserProfileIsFollowingResponseDto();

        // when
        UserProfileResponseDto userProfile =
            userService.getUserProfile(loginUser, target.getName());

        // then
        assertThat(userProfile)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("사용자는 유저 이름으로 검색하여 유저의 프로필을 조회할 수 있다. - 팔로잉을 하지 않은 경우")
    @Test
    void getUserProfile_FindByNameInCaseOfLoginUserIsNotFollowing_Success() {
        // given
        AppUser loginUser = new LoginUser("testUser", "Bearer testToken");

        userRepository.save(UserFactory.user("testUser"));
        userRepository.save(UserFactory.user("testUser2"));

        UserProfileResponseDto responseDto =
            UserFactory.mockLoginUserProfileIsNotFollowingResponseDto();

        // when
        UserProfileResponseDto userProfile = userService.getUserProfile(loginUser, "testUser2");

        // then
        assertThat(userProfile)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("게스트는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_FindByInvalidNameInCaseOfGuestUser_400Exception() {
        // given
        AppUser guestUser = new GuestUser();

        // when
        assertThatThrownBy(() -> {
            userService.getUserProfile(guestUser, "invalidName");
        }).isInstanceOf(InvalidUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("유효하지 않은 유저입니다.");
    }

    @DisplayName("사용자는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_FindByInvalidNameInCaseOfLoginUser_400Exception() {
        // given
        AppUser guestUser = new LoginUser("testUser", "Bearer testToken");

        // when
        assertThatThrownBy(() -> {
            userService.getUserProfile(guestUser, "invalidName");
        }).isInstanceOf(InvalidUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("유효하지 않은 유저입니다.");
    }

    @DisplayName("source 유저는 target 유저를 팔로우할 수 있다.")
    @Test
    void followUser_SourceToTarget_Success() {
        // given
        AuthUserRequestDto requestDto = new AuthUserRequestDto("testUser");

        userRepository.save(UserFactory.user("testUser"));
        User target = userRepository.save(UserFactory.user("testUser2"));

        // when
        FollowResponseDto responseDto = userService.followUser(requestDto, target.getName());

        // then
        assertThat(responseDto.getFollowerCount()).isEqualTo(1);
        assertThat(responseDto.isFollowing()).isTrue();
    }

    @DisplayName("이미 팔로우 중이라면 팔로우할 수 없다. - 400 예외")
    @Test
    void followUser_ExistingFollow_400Exception() {
        // given
        AuthUserRequestDto requestDto = new AuthUserRequestDto("testUser");

        userRepository.save(UserFactory.user("testUser"));
        User target = userRepository.save(UserFactory.user("testUser2"));

        userService.followUser(requestDto, target.getName());

        // when
        assertThatThrownBy(() -> {
            userService.followUser(requestDto, target.getName());
        }).isInstanceOf(DuplicateFollowException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("이미 팔로우 중 입니다.");
    }

    @DisplayName("source 유저는 target 유저를 언팔로우할 수 있다.")
    @Test
    void unfollowUser_SourceToTarget_Success() {
        // given
        AuthUserRequestDto requestDto = new AuthUserRequestDto("testUser");

        userRepository.save(UserFactory.user("testUser"));
        User target = userRepository.save(UserFactory.user("testUser2"));

        userService.followUser(requestDto, target.getName());

        // when
        FollowResponseDto responseDto = userService.unfollowUser(requestDto, target.getName());

        // then
        assertThat(responseDto.getFollowerCount()).isEqualTo(0);
        assertThat(responseDto.isFollowing()).isFalse();
    }

    @DisplayName("이미 언팔로우 중이라면 언팔로우할 수 없다. - 400 예외")
    @Test
    void unfollowUser_NotExistingFollow_400Exception() {
        // given
        AuthUserRequestDto requestDto = new AuthUserRequestDto("testUser");

        userRepository.save(UserFactory.user("testUser"));
        User target = userRepository.save(UserFactory.user("testUser2"));

        // when
        assertThatThrownBy(() -> {
            userService.unfollowUser(requestDto, target.getName());
        }).isInstanceOf(InvalidFollowException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0003")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("존재하지 않는 팔로우 입니다.");
    }
}
