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
import com.woowacourse.pickgit.user.application.dto.AuthUserResponseDto;
import com.woowacourse.pickgit.user.application.dto.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.UserProfileResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@Import(InfrastructureTestConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    private static final String NAME = "yjksw";
    private static final String IMAGE = "http://img.com";
    private static final String DESCRIPTION = "The Best";
    private static final String GITHUB_URL = "https://github.com/yjksw";
    private static final String COMPANY = "woowacourse";
    private static final String LOCATION = "Seoul";
    private static final String WEBSITE = "www.pick-git.com";
    private static final String TWITTER = "pick-git twitter";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("개인 프로필 정보를 성공적으로 가져온다.")
    @Test
    public void getMyUserProfile_FindUserInfoByName_Success() {
        //given
        AuthUserResponseDto authUserResponseDto = new AuthUserResponseDto(NAME);
        userRepository.save(UserFactory.user());
        UserProfileResponseDto expectedUserProfileDto = new UserProfileResponseDto(
            NAME, IMAGE, DESCRIPTION,
            0, 0, 0,
            GITHUB_URL, COMPANY, LOCATION, WEBSITE, TWITTER, null
        );

        //when
        UserProfileResponseDto actualUserProfileDto = userService
            .getMyUserProfile(authUserResponseDto);

        //then
        assertThat(actualUserProfileDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedUserProfileDto);
    }

    @DisplayName("게스트 유저가 프로필 조회시 프로필 정보를 성공적으로 가져온다.")
    @Test
    public void getUserProfile_GuestFindUserInfoByName_Success() {
        //given
        AppUser guestUser = new GuestUser();
        userRepository.save(UserFactory.user());
        UserProfileResponseDto expectedUserProfileDto = new UserProfileResponseDto(
            NAME, IMAGE, DESCRIPTION,
            0, 0, 0,
            GITHUB_URL, COMPANY, LOCATION, WEBSITE, TWITTER, null
        );

        //when
        UserProfileResponseDto actualUserProfileDto = userService.getUserProfile(guestUser, NAME);

        //then
        assertThat(actualUserProfileDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedUserProfileDto);
    }

    @DisplayName("로그인 유저가 팔로우 하는 프로필 조회시 프로필 정보를 성공적으로 가져온다.")
    @Test
    public void getUserProfile_FindFollowingUserInfoByName_Success() {
        //given
        AppUser loginUser = new LoginUser(NAME, "token");
        User source = userRepository.save(UserFactory.user(NAME));
        User target = userRepository.save(UserFactory.user("testUser2"));

        AuthUserResponseDto authUserResponseDto = new AuthUserResponseDto(source.getName());
        userService.followUser(authUserResponseDto, target.getName());

        UserProfileResponseDto expectedUserProfileDto = new UserProfileResponseDto(
            target.getName(), target.getImage(), target.getDescription(),
            1, 0, 0,
            target.getGithubUrl(), target.getCompany(), target.getLocation(),
            target.getWebsite(), target.getTwitter(), true
        );

        //when
        UserProfileResponseDto actualUserProfileDto = userService
            .getUserProfile(loginUser, target.getName());

        //then
        assertThat(actualUserProfileDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedUserProfileDto);
    }

    @DisplayName("로그인 유저가 팔로우하고 있지 않은 프로필 조회시 프로필 정보를 성공적으로 가져온다.")
    @Test
    public void getUserProfile_FindUnfollowingUserInfoByName_Success() {
        //given
        AppUser loginUser = new LoginUser(NAME, "token");
        userRepository.save(UserFactory.user(1L, NAME));
        userRepository.save(UserFactory.user(2L, "testUser2"));

        UserProfileResponseDto expectedUserProfileDto = new UserProfileResponseDto(
            NAME, IMAGE, DESCRIPTION,
            0, 0, 0,
            GITHUB_URL, COMPANY, LOCATION, WEBSITE, TWITTER, false
        );

        //when
        UserProfileResponseDto actualUserProfileDto = userService.getUserProfile(loginUser, NAME);

        //then
        assertThat(actualUserProfileDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedUserProfileDto);
    }

    @DisplayName("존재하지 않는 유저 이름으로 프로필 조회시 예외가 발생한다.")
    @Test
    void getUserProfile_FindUserInfoByInvalidName_Success() {
        //given
        //when
        //then
        AppUser appUser = new GuestUser();
        assertThatThrownBy(
            () -> userService.getUserProfile(appUser, "InvalidName")
        ).hasMessage(new InvalidUserException().getMessage());
    }

    @DisplayName("Source 유저가 Target 유저를 follow 하면 성공한다.")
    @Test
    void followUser_ValidUser_Success() {
        //given
        String targetName = "pickgit";
        userRepository.save(UserFactory.user(NAME));
        userRepository.save(UserFactory.user(targetName));
        AuthUserResponseDto authUserResponseDto = new AuthUserResponseDto(NAME);

        //when
        FollowResponseDto followResponseDto = userService.followUser(
            authUserResponseDto, targetName);

        //then
        assertThat(followResponseDto.getFollowerCount()).isEqualTo(1);
        assertThat(followResponseDto.isFollowing()).isTrue();
    }

    @DisplayName("이미 존재하는 Follow 추가 시 예외가 발생한다.")
    @Test
    void followUser_ExistingFollow_ExceptionThrown() {
        //given
        String targetName = "pickgit";
        userRepository.save(UserFactory.user(NAME));
        userRepository.save(UserFactory.user(targetName));
        AuthUserResponseDto authUserResponseDto = new AuthUserResponseDto(NAME);

        userService.followUser(authUserResponseDto, targetName);

        //when
        //then
        assertThatThrownBy(
            () -> userService.followUser(authUserResponseDto, targetName)
        ).hasMessage(new DuplicateFollowException().getMessage());
    }

    @DisplayName("Source 유저가 Target 유저를 unfollow 하면 성공한다.")
    @Test
    void unfollowUser_ValidUser_Success() {
        //given
        String targetName = "pickgit";
        userRepository.save(UserFactory.user(NAME));
        userRepository.save(UserFactory.user(targetName));
        AuthUserResponseDto authUserResponseDto = new AuthUserResponseDto(NAME);

        userService.followUser(authUserResponseDto, targetName);

        //when
        FollowResponseDto followResponseDto = userService
            .unfollowUser(authUserResponseDto, targetName);

        //then
        assertThat(followResponseDto.getFollowerCount()).isEqualTo(0);
        assertThat(followResponseDto.isFollowing()).isFalse();
    }

    @DisplayName("존재하지 않는 Follow 관계를 unfollow 하면 예외가 발생한다.")
    @Test
    void unfollowUser_NotExistingFollow_ExceptionThrown() {
        //given
        String targetName = "pickgit";
        userRepository.save(UserFactory.user(NAME));
        userRepository.save(UserFactory.user(targetName));
        AuthUserResponseDto authUserResponseDto = new AuthUserResponseDto(NAME);

        //when
        //then
        assertThatThrownBy(
            () -> userService.unfollowUser(authUserResponseDto, targetName)
        ).hasMessage(new InvalidFollowException().getMessage());
    }
}
