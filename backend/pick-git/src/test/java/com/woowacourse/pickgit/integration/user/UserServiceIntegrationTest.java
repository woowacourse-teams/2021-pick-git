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

    @DisplayName("????????? ????????? ???????????? ?????? ?????? ???????????? ???????????? ????????? ??? ??????. - 400 ??????")
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
            .hasMessage("???????????? ?????? ???????????????.");
    }

    @DisplayName("???????????? ????????? ???????????? ??? ??????.")
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

    @DisplayName("????????? ????????? ???????????? ?????? ????????? ?????? ???????????? ??? ??????. - 400 ???")
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
            .hasMessage("???????????? ?????? ???????????????.");
    }

    @DisplayName("????????? ????????? ?????? ????????? ???????????? ??? ??????. - 400 ??????")
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
            .hasMessage("?????? Source ??? Target ???????????????.");
    }

    @DisplayName("????????? ????????? ??????????????? ?????? Target ????????? ???????????? ??? ??????.")
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

    @DisplayName("????????? ????????? ?????? ????????? ?????? Target ????????? ???????????? ??? ??????. - 400 ??????")
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
            .hasMessage("?????? ????????? ??? ?????????.");
    }

    @DisplayName("???????????? ????????? ??????????????? ??? ??????.")
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

    @DisplayName("????????? ????????? ???????????? ?????? ????????? ?????? ??????????????? ??? ??????. - 400 ??????")
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
            .hasMessage("???????????? ?????? ???????????????.");
    }

    @DisplayName("????????? ????????? ?????? ????????? ??????????????? ??? ??????. - 400 ??????")
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
            .hasMessage("?????? Source ??? Target ???????????????.");
    }

    @DisplayName("????????? ????????? ?????????????????? Target ????????? ??????????????? ??? ??????. - 400 ??????")
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
            .hasMessage("???????????? ?????? ????????? ?????????.");
    }

    @DisplayName("????????? ????????? ????????? ?????? Target ????????? ???????????? ??? ??? ??????.")
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

    @DisplayName("???????????? ?????? ????????? ????????? ??? ??????.")
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

    @DisplayName("???????????? ?????? ?????? ???????????? ?????? ????????? ????????? ??? ??????. - 400 ??????")
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
            .hasMessage("???????????? ?????? ???????????????.");
    }
    
    @DisplayName("????????? ????????? ???????????? ????????? ??? ??????.")
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

    @DisplayName("????????? ????????? ??? ??? ????????? ????????? ??? ??????.")
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
