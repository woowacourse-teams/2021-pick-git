package com.woowacourse.pickgit.unit.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.AuthUserServiceDto;
import com.woowacourse.pickgit.user.application.dto.FollowServiceDto;
import com.woowacourse.pickgit.user.application.dto.UserProfileServiceDto;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.exception.user.DuplicateFollowException;
import com.woowacourse.pickgit.exception.user.InvalidFollowException;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceMockTest {

    private static final String NAME = "yjksw";
    private static final String IMAGE = "http://img.com";
    private static final String DESCRIPTION = "The Best";
    private static final String GITHUB_URL = "https://github.com/yjksw";
    private static final String COMPANY = "woowacourse";
    private static final String LOCATION = "Seoul";
    private static final String WEBSITE = "www.pick-git.com";
    private static final String TWITTER = "pick-git twitter";

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private UserFactory userFactory;

    @BeforeEach
    void setUp() {
        this.userFactory = new UserFactory();
    }

    @DisplayName("본인의 프로필 정보를 성공적으로 가져온다.")
    @Test
    void name() {

    }

    @DisplayName("유저이름으로 검색한 User 기반으로 프로필 정보를 성공적으로 가져온다.")
    @Test
    void getUserProfile_FindUserInfoByName_Success() {
        //given
        AppUser appUser = new GuestUser();
        given(
            userRepository.findByBasicProfile_Name(anyString())
        ).willReturn(Optional.of(userFactory.user()));

        UserProfileServiceDto expectedUserProfileDto = new UserProfileServiceDto(
            NAME, IMAGE, DESCRIPTION,
            0, 0, 0,
            GITHUB_URL, COMPANY, LOCATION, WEBSITE, TWITTER, null
        );

        //when
        UserProfileServiceDto actualUserProfileDto = userService.getUserProfile(appUser, NAME);

        //then
        assertThat(actualUserProfileDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedUserProfileDto);

        verify(userRepository, times(1)).findByBasicProfile_Name(anyString());
    }

    @DisplayName("존재하지 않는 유저 이름으로 프로필 조회시 예외가 발생한다.")
    @Test
    void getUserProfile_FindUserInfoByInvalidName_Success() {
        //given
        AppUser appUser = new GuestUser();
        //when
        //then
        assertThatThrownBy(
            () -> userService.getUserProfile(appUser, "InvalidName")
        ).hasMessage(new InvalidUserException().getMessage());

        verify(userRepository, times(1)).findByBasicProfile_Name(anyString());
    }

    @DisplayName("Source 유저가 Target 유저를 follow 하면 성공한다.")
    @Test
    void followUser_ValidUser_Success() {
        //given
        AuthUserServiceDto authUserServiceDto = new AuthUserServiceDto(NAME);
        String targetName = "target";

        given(
            userRepository.findByBasicProfile_Name(NAME)
        ).willReturn(Optional.of(userFactory.user()));

        given(
            userRepository.findByBasicProfile_Name("target")
        ).willReturn(Optional.of(userFactory.anotherUser()));

        //when
        FollowServiceDto followServiceDto = userService.followUser(authUserServiceDto, targetName);

        //then
        assertThat(followServiceDto.getFollowerCount()).isEqualTo(1);
        assertThat(followServiceDto.isFollowing()).isTrue();

        verify(userRepository, times(2)).findByBasicProfile_Name(anyString());
    }

    @DisplayName("이미 존재하는 Follow 추가 시 예외가 발생한다.")
    @Test
    void followUser_ExistingFollow_ExceptionThrown() {
        //given
        AuthUserServiceDto authUserServiceDto = new AuthUserServiceDto(NAME);
        String targetName = "target";

        given(
            userRepository.findByBasicProfile_Name(NAME)
        ).willReturn(Optional.of(userFactory.user()));

        given(
            userRepository.findByBasicProfile_Name("target")
        ).willReturn(Optional.of(userFactory.anotherUser()));

        userService.followUser(authUserServiceDto, targetName);

        //when
        //then
        assertThatThrownBy(
            () -> userService.followUser(authUserServiceDto, targetName)
        ).hasMessage(new DuplicateFollowException().getMessage());

        verify(userRepository, times(4)).findByBasicProfile_Name(anyString());
    }

    @DisplayName("Source 유저가 Target 유저를 unfollow 하면 성공한다.")
    @Test
    void unfollowUser_ValidUser_Success() {
        //given
        AuthUserServiceDto authUserServiceDto = new AuthUserServiceDto(NAME);
        String targetName = "target";

        given(
            userRepository.findByBasicProfile_Name(NAME)
        ).willReturn(Optional.of(userFactory.user()));

        given(
            userRepository.findByBasicProfile_Name("target")
        ).willReturn(Optional.of(userFactory.anotherUser()));

        userService.followUser(authUserServiceDto, targetName);

        //when
        FollowServiceDto followServiceDto = userService
            .unfollowUser(authUserServiceDto, targetName);

        //then
        assertThat(followServiceDto.getFollowerCount()).isEqualTo(0);
        assertThat(followServiceDto.isFollowing()).isFalse();

        verify(userRepository, times(4)).findByBasicProfile_Name(anyString());
    }

    @DisplayName("존재하지 않는 Follow 관계를 unfollow 하면 예외가 발생한다.")
    @Test
    void unfollowUser_NotExistingFollow_ExceptionThrown() {
        //given
        AuthUserServiceDto authUserServiceDto = new AuthUserServiceDto(NAME);
        String targetName = "target";

        given(
            userRepository.findByBasicProfile_Name(NAME)
        ).willReturn(Optional.of(userFactory.user()));

        given(
            userRepository.findByBasicProfile_Name("target")
        ).willReturn(Optional.of(userFactory.anotherUser()));

        //when
        //then
        assertThatThrownBy(
            () -> userService.unfollowUser(authUserServiceDto, targetName)
        ).hasMessage(new InvalidFollowException().getMessage());

        verify(userRepository, times(2)).findByBasicProfile_Name(anyString());
    }
}
