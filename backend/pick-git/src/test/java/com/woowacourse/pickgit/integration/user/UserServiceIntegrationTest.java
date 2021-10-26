package com.woowacourse.pickgit.integration.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.user.DuplicateFollowException;
import com.woowacourse.pickgit.exception.user.InvalidFollowException;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.exception.user.SameSourceTargetUserException;
import com.woowacourse.pickgit.integration.IntegrationTest;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserForUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileImageEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileImageEditResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import com.woowacourse.pickgit.user.presentation.dto.UserAssembler;
import com.woowacourse.pickgit.user.presentation.dto.request.ContributionRequestDto;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class UserServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("로그인 유저는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_FindByInvalidNameInCaseOfLoginUser_400Exception() {
        // given
        User loginUser = userRepository.save(UserFactory.user("testUser"));
        AuthUserForUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        // when
        assertThatThrownBy(() ->
            userService.getUserProfile(authUserRequestDto, "invalidName"))
            .isInstanceOf(InvalidUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("유효하지 않은 유저입니다.");
    }

    @DisplayName("비로그인 유저는 팔로우할 수 없다.")
    @Test
    void follow_Guest_Failure() {
        // given
        AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();

        FollowRequestDto requestDto = FollowRequestDto.builder()
            .authUserRequestDto(authUserRequestDto)
            .targetName("testUser")
            .githubFollowing(false)
            .build();

        // when, then
        assertThatCode(() -> userService.followUser(requestDto))
            .isInstanceOf(InvalidUserException.class);
    }

    @DisplayName("로그인 유저는 존재하지 않는 유저에 대해 팔로우할 수 없다. - 400 예")
    @Test
    void follow_FindByInvalidName_400Exception() {
        // given
        User loginUser = userRepository.save(UserFactory.user("testUser"));
        AuthUserForUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        FollowRequestDto requestDto = FollowRequestDto.builder()
            .authUserRequestDto(authUserRequestDto)
            .targetName("kevin")
            .githubFollowing(false)
            .build();

        // when, then
        assertThatCode(() -> userService.followUser(requestDto))
            .isInstanceOf(InvalidUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("유효하지 않은 유저입니다.");
    }

    @DisplayName("로그인 유저는 자기 자신을 팔로우할 수 없다. - 400 예외")
    @Test
    void follow_SameUser_400Exception() {
        //given
        User loginUser = userRepository.save(UserFactory.user("testUser"));
        AuthUserForUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        FollowRequestDto requestDto = FollowRequestDto.builder()
            .authUserRequestDto(authUserRequestDto)
            .targetName(loginUser.getName())
            .githubFollowing(false)
            .build();

        // when, then
        assertThatCode(
            () -> userService.followUser(requestDto))
            .isInstanceOf(SameSourceTargetUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0004")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("같은 Source 와 Target 유저입니다.");
    }

    @DisplayName("로그인 유저는 팔로잉하지 않는 Target 유저를 팔로우할 수 있다.")
    @Test
    void followUser_SourceToTarget_Success() {
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

        // when
        FollowResponseDto responseDto = userService
            .followUser(requestDto);

        // then
        assertThat(responseDto.getFollowerCount()).isOne();
        assertThat(responseDto.isFollowing()).isTrue();
    }

    @DisplayName("로그인 유저는 이미 팔로우 중인 Target 유저를 팔로우할 수 없다. - 400 예외")
    @Test
    void followUser_ExistingFollow_400Exception() {
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

        // when
        assertThatThrownBy(() ->
            userService.followUser(requestDto))
            .isInstanceOf(DuplicateFollowException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("이미 팔로우 중 입니다.");
    }

    @DisplayName("비로그인 유저는 언팔로우할 수 없다.")
    @Test
    void unfollow_Guest_Failure() {
        // given
        AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();

        FollowRequestDto requestDto = FollowRequestDto.builder()
            .authUserRequestDto(authUserRequestDto)
            .targetName("testUser")
            .githubFollowing(false)
            .build();

        // when, then
        assertThatCode(() -> userService.unfollowUser(requestDto))
            .isInstanceOf(InvalidUserException.class);
    }

    @DisplayName("로그인 유저는 존재하지 않는 유저에 대해 언팔로우할 수 없다. - 400 예외")
    @Test
    void unfollow_FindByInvalidName_400Exception() {
        //given
        User loginUser = userRepository.save(UserFactory.user("testUser"));
        AuthUserForUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        FollowRequestDto requestDto = FollowRequestDto.builder()
            .authUserRequestDto(authUserRequestDto)
            .targetName("kevin")
            .githubFollowing(false)
            .build();

        // when, then
        assertThatCode(() -> userService.followUser(requestDto))
            .isInstanceOf(InvalidUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("유효하지 않은 유저입니다.");
    }

    @DisplayName("로그인 유저는 자기 자신을 언팔로우할 수 없다. - 400 예외")
    @Test
    void unfollow_SameUser_400Exception() {
        //given
        User loginUser = userRepository.save(UserFactory.user("testUser"));
        AuthUserForUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        FollowRequestDto requestDto = FollowRequestDto.builder()
            .authUserRequestDto(authUserRequestDto)
            .targetName(loginUser.getName())
            .githubFollowing(false)
            .build();

        // when, then
        assertThatCode(
            () -> userService.unfollowUser(requestDto))
            .isInstanceOf(SameSourceTargetUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0004")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("같은 Source 와 Target 유저입니다.");
    }

    @DisplayName("로그인 유저는 언팔로우중인 Target 유저를 언팔로우할 수 없다. - 400 예외")
    @Test
    void unfollowUser_NotExistingFollow_400Exception() {
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

        // when, then
        assertThatThrownBy(
            () -> userService.unfollowUser(requestDto))
            .isInstanceOf(InvalidFollowException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0003")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("존재하지 않는 팔로우 입니다.");
    }

    @DisplayName("로그인 유저는 팔로우 중인 Target 유저를 언팔로우 할 수 있다.")
    @Test
    void unfollowUser_SourceToTarget_Success() {
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

        // when
        FollowResponseDto responseDto = userService
            .unfollowUser(requestDto);

        // then
        assertThat(responseDto.getFollowerCount()).isZero();
        assertThat(responseDto.isFollowing()).isFalse();
    }

    @DisplayName("사용자는 활동 통계를 조회할 수 있다.")
    @Test
    void calculateContributions_LoginUser_Success() {
        // given
        userRepository.save(UserFactory.user());

        ContributionRequestDto requestDto = UserFactory.mockContributionRequestDto();
        ContributionResponseDto contributions = UserFactory.mockContributionResponseDto();

        // when
        ContributionResponseDto responseDto = userService.calculateContributions(requestDto);

        // then
        assertThat(responseDto)
            .usingRecursiveComparison()
            .isEqualTo(contributions);
    }

    @DisplayName("존재하지 않은 유저 이름으로 활동 통계를 조회할 수 없다. - 400 예외")
    @Test
    void calculateContributions_InvalidUsername_400Exception() {
        // given
        ContributionRequestDto requestDto = UserFactory.mockContributionRequestDto();

        // when
        assertThatThrownBy(() -> {
            userService.calculateContributions(requestDto);
        }).isInstanceOf(InvalidUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("유효하지 않은 유저입니다.");
    }
    
    @DisplayName("자신의 프로필 이미지를 수정할 수 있다.")
    @Test
    void editProfileImage_LoginUser_Success() throws IOException {
        // given
        User user = UserFactory.user("testUser");
        AuthUserForUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto("testUser");
        File file = FileFactory.getTestImage1File();

        userRepository.save(user);

        // when
        ProfileImageEditRequestDto requestDto = ProfileImageEditRequestDto
            .builder()
            .image(new FileInputStream(file).readAllBytes())
            .build();
        ProfileImageEditResponseDto responseDto =
            userService.editProfileImage(authUserRequestDto, requestDto);

        // then
        assertThat(responseDto.getImageUrl()).isNotBlank();
    }

    @DisplayName("자신의 프로필 한 줄 소개를 수정할 수 있다.")
    @Test
    void editProfileDescription_LoginUser_Success() {
        // given
        User user = UserFactory.user("testUser");
        AuthUserForUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto("testUser");
        String description = "updated description";

        userRepository.save(user);

        // when
        String updatedDescription = userService.editProfileDescription(
            authUserRequestDto,
            description
        );

        // then
        assertThat(updatedDescription).isEqualTo(description);
    }

    private AuthUserForUserRequestDto createLoginAuthUserRequestDto(String username) {
        AppUser appUser = new LoginUser(username, "Bearer testToken");
        return UserAssembler.authUserForUserRequestDto(appUser);
    }

    private AuthUserForUserRequestDto createGuestAuthUserRequestDto() {
        return UserAssembler.authUserForUserRequestDto(new GuestUser());
    }
}
