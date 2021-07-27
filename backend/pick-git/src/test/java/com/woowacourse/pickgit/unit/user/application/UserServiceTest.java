package com.woowacourse.pickgit.unit.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.user.DuplicateFollowException;
import com.woowacourse.pickgit.exception.user.InvalidFollowException;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ContributionRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.domain.PlatformExtractor;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.dto.CountResponseDto;
import com.woowacourse.pickgit.user.domain.dto.StarResponseDto;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlatformExtractor platformExtractor;

    @DisplayName("사용자는 내 이름으로 내 프로필을 조회할 수 있다.")
    @Test
    void getMyUserProfile_WithMyName_Success() {
        // given
        AuthUserRequestDto requestDto = new AuthUserRequestDto("testUser");
        User testUser = UserFactory.user();

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(testUser));

        UserProfileResponseDto responseDto = UserFactory.mockLoginUserProfileResponseDto();

        // when
        UserProfileResponseDto myUserProfile = userService.getMyUserProfile(requestDto);

        // then
        assertThat(myUserProfile)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);

        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
    }

    @DisplayName("게스트는 유저 이름으로 검색하여 유저의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_FindByNameInCaseOfGuestUser_Success() {
        //given
        AppUser guestUser = new GuestUser();
        User testUser = UserFactory.user("testUser");

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(testUser));

        UserProfileResponseDto responseDto = UserFactory.mockGuestUserProfileResponseDto();

        //when
        UserProfileResponseDto userProfile = userService.getUserProfile(guestUser, "guestUser");

        //then
        assertThat(userProfile)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);

        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
    }

    @DisplayName("사용자는 유저 이름으로 검색하여 유저의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_FindByNameInCaseOfLoginUser_Success() {
        //given
        AppUser loginUser = new LoginUser("testUser", "Bearer testToken");
        User testUser = UserFactory.user("testUser2");

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(testUser));

        UserProfileResponseDto responseDto =
            UserFactory.mockLoginUserProfileIsNotFollowingResponseDto();

        //when
        UserProfileResponseDto userProfile = userService.getUserProfile(loginUser, "testUser2");

        //then
        assertThat(userProfile)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);

        verify(userRepository, times(2))
            .findByBasicProfile_Name(anyString());
    }

    @DisplayName("게스트는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_FindByInvalidNameInCaseOfGuestUser_400Exception() {
        //given
        AppUser guestUser = new GuestUser();

        //when
        assertThatThrownBy(
            () -> userService.getUserProfile(guestUser, "InvalidName")
        ).isInstanceOf(InvalidUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("유효하지 않은 유저입니다.");

        // then
        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
    }

    @DisplayName("사용자는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_FindByInvalidNameInCaseOfLoginUser_400Exception() {
        //given
        AppUser loginUser = new LoginUser("testUser", "Bearer testToken");

        //when
        assertThatThrownBy(
            () -> userService.getUserProfile(loginUser, "InvalidName")
        ).isInstanceOf(InvalidUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("유효하지 않은 유저입니다.");

        // then
        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
    }

    @DisplayName("누구든지 활동 통계를 조회할 수 있다.")
    @Test
    void calculateContributions_Anyone_Success() {
        // given
        ContributionRequestDto requestDto = new ContributionRequestDto("testUser");
        User user = UserFactory.user();

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
        given(platformExtractor.extractStars(anyString()))
            .willReturn(List.of(new StarResponseDto(5), new StarResponseDto(6)));
        given(platformExtractor.extractCount(anyString(), anyString()))
            .willReturn(new CountResponseDto(48));
        given(platformExtractor.extractCount(anyString(), anyString()))
            .willReturn(new CountResponseDto(48));
        given(platformExtractor.extractCount(anyString(), anyString()))
            .willReturn(new CountResponseDto(48));
        given(platformExtractor.extractCount(anyString(), anyString()))
            .willReturn(new CountResponseDto(48));

        ContributionResponseDto responseDto = UserFactory.mockContributionResponseDto();

        // when
        ContributionResponseDto contributions =
            userService.calculateContributions(requestDto);

        // then
        assertThat(contributions)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);

        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
        verify(platformExtractor, times(1))
            .extractStars(anyString());
        verify(platformExtractor, times(4))
            .extractCount(anyString(), anyString());
    }

    @DisplayName("존재하지 않는 유저 이름으로 활동 통계를 조회할 수 없다. - 400 예외")
    @Test
    void calculateContributions_InvalidUsername_400Exception() {
        // when
        assertThatThrownBy(() -> {
            userService.calculateContributions(new ContributionRequestDto("invalidName"));
        }).isInstanceOf(InvalidUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("유효하지 않은 유저입니다.");

        // then
        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
    }

    @DisplayName("source 유저는 target 유저를 팔로우할 수 있다.")
    @Test
    void followUser_SourceToTarget_Success() {
        //given
        AuthUserRequestDto requestDto = new AuthUserRequestDto("testUser");

        given(userRepository.findByBasicProfile_Name("testUser"))
            .willReturn(Optional.of(UserFactory.user(1L, "testUser")));
        given(userRepository.findByBasicProfile_Name("targetUser"))
            .willReturn(Optional.of(UserFactory.user(2L, "targetUser")));

        //when
        FollowResponseDto responseDto = userService.followUser(requestDto, "targetUser");

        //then
        assertThat(responseDto.getFollowerCount()).isEqualTo(1);
        assertThat(responseDto.isFollowing()).isTrue();

        verify(userRepository, times(2))
            .findByBasicProfile_Name(anyString());
    }

    @DisplayName("이미 팔로우 중이라면 팔로우를 할 수 없다. - 400 예외")
    @Test
    void followUser_ExistingFollow_400Exception() {
        //given
        AuthUserRequestDto requestDto = new AuthUserRequestDto("testUser");

        given(userRepository.findByBasicProfile_Name("testUser"))
            .willReturn(Optional.of(UserFactory.user(1L, "testUser")));
        given(userRepository.findByBasicProfile_Name("targetUser"))
            .willReturn(Optional.of(UserFactory.user(2L, "targetUser")));

        userService.followUser(requestDto, "targetUser");

        //when
        assertThatThrownBy(
            () -> userService.followUser(requestDto, "targetUser")
        ).isInstanceOf(DuplicateFollowException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("이미 팔로우 중 입니다.");

        // then
        verify(userRepository, times(4))
            .findByBasicProfile_Name(anyString());
    }

    @DisplayName("source 유저는 target 유저를 언팔로우할 수 있다.")
    @Test
    void unfollowUser_SourceToTarget_Success() {
        //given
        AuthUserRequestDto requestDto = new AuthUserRequestDto("testUser");

        given(userRepository.findByBasicProfile_Name("testUser"))
            .willReturn(Optional.of(UserFactory.user(1L, "testUser")));
        given(userRepository.findByBasicProfile_Name("targetUser"))
            .willReturn(Optional.of(UserFactory.user(2L, "targetUser")));

        userService.followUser(requestDto, "targetUser");

        //when
        FollowResponseDto responseDto = userService.unfollowUser(requestDto, "targetUser");

        //then
        assertThat(responseDto.getFollowerCount()).isEqualTo(0);
        assertThat(responseDto.isFollowing()).isFalse();

        verify(userRepository, times(4))
            .findByBasicProfile_Name(anyString());
    }

    @DisplayName("이미 언팔로우 중이라면 언팔로우할 수 없다. - 400 예외")
    @Test
    void unfollowUser_NotExistingFollow_400Exception() {
        //given
        AuthUserRequestDto requestDto = new AuthUserRequestDto("testUser");

        given(userRepository.findByBasicProfile_Name("testUser"))
            .willReturn(Optional.of(UserFactory.user(1L, "testUser")));
        given(userRepository.findByBasicProfile_Name("targetUser"))
            .willReturn(Optional.of(UserFactory.user(2L, "targetUser")));

        //when
        assertThatThrownBy(
            () -> userService.unfollowUser(requestDto, "targetUser")
        ).isInstanceOf(InvalidFollowException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0003")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("존재하지 않는 팔로우 입니다.");

        // then
        verify(userRepository, times(2))
            .findByBasicProfile_Name(anyString());
    }
}
