package com.woowacourse.pickgit.unit.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.user.DuplicateFollowException;
import com.woowacourse.pickgit.exception.user.InvalidFollowException;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.exception.user.SameSourceTargetUserException;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserForUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileImageEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileImageEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import com.woowacourse.pickgit.user.domain.follow.PlatformFollowingRequester;
import com.woowacourse.pickgit.user.domain.profile.PickGitProfileStorage;
import com.woowacourse.pickgit.user.domain.search.UserSearchEngine;
import com.woowacourse.pickgit.user.domain.contribution.Contribution;
import com.woowacourse.pickgit.user.domain.contribution.ContributionCategory;
import com.woowacourse.pickgit.user.domain.contribution.PlatformContributionCalculator;
import com.woowacourse.pickgit.user.presentation.dto.UserAssembler;
import com.woowacourse.pickgit.user.presentation.dto.request.ContributionRequestDto;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSearchEngine userSearchEngine;

    @Mock
    private PickGitProfileStorage pickGitProfileStorage;

    @Mock
    private PlatformContributionCalculator platformContributionCalculator;

    @Mock
    private PlatformFollowingRequester platformFollowingRequester;

    @DisplayName("getMyUserProfile ????????????")
    @Nested
    class Describe_getMyUserProfile {

        @DisplayName("????????? ???????????? ???")
        @Nested
        class Context_Login {

            @DisplayName("???????????? ??? ???????????? ????????? ??? ??????.")
            @Test
            void getMyUserProfile_WithMyName_Success() {
                // given
                User loginUser = UserFactory.user();
                String username = loginUser.getName();
                AuthUserForUserRequestDto requestDto = createLoginAuthUserRequestDto(username);

                given(userRepository.findByBasicProfile_Name(username))
                    .willReturn(Optional.of(loginUser));

                UserProfileResponseDto responseDto = UserFactory.mockLoginUserProfileResponseDto();

                // when
                UserProfileResponseDto myUserProfile = userService.getMyUserProfile(requestDto);

                // then
                assertThat(myUserProfile)
                    .usingRecursiveComparison()
                    .isEqualTo(responseDto);

                verify(userRepository, times(1))
                    .findByBasicProfile_Name(username);
            }
        }

        @DisplayName("?????????????????? ?????????")
        @Nested
        class Context_Guest {

            @DisplayName("???????????? ??? ???????????? ????????? ??? ??????.")
            @Test
            void getMyUserProfile_Guest_Failure() {
                // given
                AuthUserForUserRequestDto requestDto = createGuestAuthUserRequestDto();

                // when, then
                assertThatCode(() -> userService.getMyUserProfile(requestDto))
                    .isInstanceOf(InvalidUserException.class);
            }
        }
    }

    @DisplayName("getUserProfile ????????????")
    @Nested
    class Describe_getUserProfile {

        @DisplayName("????????? ????????? ???")
        @Nested
        class Context_GuestUser {

            @DisplayName("?????? ???????????? ???????????? ????????? ???????????? ????????? ??? ??????.")
            @Test
            void getUserProfile_FindByNameInCaseOfGuestUser_Success() {
                //given
                AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
                User targetUser = UserFactory.user("testUser");
                String targetUsername = targetUser.getName();

                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.of(targetUser));

                UserProfileResponseDto responseDto = UserFactory.mockGuestUserProfileResponseDto();

                //when
                UserProfileResponseDto userProfile = userService
                    .getUserProfile(authUserRequestDto, targetUsername);

                //then
                assertThat(userProfile)
                    .usingRecursiveComparison()
                    .isEqualTo(responseDto);

                verify(userRepository, times(1))
                    .findByBasicProfile_Name(targetUsername);
            }

            @DisplayName("???????????? ?????? ?????? ???????????? ???????????? ????????? ??? ??????. - 400 ??????")
            @Test
            void getUserProfile_FindByInvalidNameInCaseOfGuestUser_400Exception() {
                //given
                AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
                String invalidName = "InvalidName";

                given(userRepository.findByBasicProfile_Name(invalidName))
                    .willReturn(Optional.empty());

                //when
                assertThatThrownBy(
                    () -> userService.getUserProfile(authUserRequestDto, invalidName)
                ).isInstanceOf(InvalidUserException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("???????????? ?????? ???????????????.");

                // then
                verify(userRepository, times(1))
                    .findByBasicProfile_Name(invalidName);
            }
        }

        @DisplayName("????????? ????????? ???")
        @Nested
        class Context_LoginUser {

            @DisplayName("????????? ?????? ????????? ???????????? ????????? ??? ??????. (Github ????????? true)")
            @Test
            void getUserProfile_FindByNameInCaseOfLoginUserIsFollowing_Success() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserForUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                User targetUser = UserFactory.user(2L, "testUser2");
                String targetUsername = targetUser.getName();

                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.of(targetUser));
                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));

                FollowRequestDto requestDto = FollowRequestDto.builder()
                    .authUserRequestDto(authUserRequestDto)
                    .targetName(targetUsername)
                    .githubFollowing(false)
                    .build();

                UserProfileResponseDto responseDto =
                    UserFactory.mockLoginUserProfileIsFollowingResponseDto();

                userService.followUser(requestDto);

                //when
                UserProfileResponseDto userProfile = userService
                    .getUserProfile(authUserRequestDto, targetUsername);

                //then
                assertThat(userProfile)
                    .usingRecursiveComparison()
                    .isEqualTo(responseDto);

                verify(userRepository, times(2))
                    .findByBasicProfile_Name(targetUsername);
                verify(userRepository, times(2))
                    .findByBasicProfile_Name(loginUsername);
            }

            @DisplayName("??????????????? ?????? ????????? ???????????? ????????? ??? ??????.")
            @Test
            void getUserProfile_FindByNameInCaseOfLoginUseIsNotFollowing_Success() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserForUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                User targetUser = UserFactory.user(2L, "testUser2");
                String targetUsername = targetUser.getName();

                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.of(targetUser));
                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));

                UserProfileResponseDto responseDto =
                    UserFactory.mockLoginUserProfileIsNotFollowingResponseDto();

                //when
                UserProfileResponseDto userProfile = userService
                    .getUserProfile(authUserRequestDto, targetUsername);

                //then
                assertThat(userProfile)
                    .usingRecursiveComparison()
                    .isEqualTo(responseDto);

                verify(userRepository, times(1))
                    .findByBasicProfile_Name(targetUsername);
                verify(userRepository, times(1))
                    .findByBasicProfile_Name(loginUsername);
            }

            @DisplayName("???????????? ?????? ?????? ???????????? ???????????? ????????? ??? ??????. - 400 ??????")
            @Test
            void getUserProfile_FindByInvalidNameInCaseOfLoginUser_400Exception() {
                //given
                User loginUser = UserFactory.user("testUser");
                String loginUsername = loginUser.getName();
                AuthUserForUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                String targetUsername = "invalidname";

                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.empty());

                //when, then
                assertThatThrownBy(
                    () -> userService.getUserProfile(authUserRequestDto, targetUsername)
                ).isInstanceOf(InvalidUserException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("???????????? ?????? ???????????????.");

                verify(userRepository, times(1))
                    .findByBasicProfile_Name(targetUsername);
                verify(userRepository, times(0))
                    .findByBasicProfile_Name(loginUsername);
            }
        }
    }

    @DisplayName("followUser ????????????")
    @Nested
    class Describe_followUser {

        @DisplayName("?????????????????? ?????????")
        @Nested
        class Context_Guest {

            @DisplayName("???????????? ??? ??????.")
            @Test
            void follow_Guest_Failure() {
                // given
                AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();

                FollowRequestDto requestDto = FollowRequestDto.builder()
                    .authUserRequestDto(authUserRequestDto)
                    .targetName("testUSer")
                    .build();

                // when, then
                assertThatCode(() -> userService.followUser(requestDto))
                    .isInstanceOf(InvalidUserException.class);
            }
        }

        @DisplayName("Target ????????? ???????????? ????????????")
        @Nested
        class Context_NotExistingOtherUser {

            @DisplayName("???????????? ??? ??????. - 400 ??????")
            @Test
            void follow_FindByInvalidName_400Exception() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserForUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                String invalidTargetName = "django";

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));
                given(userRepository.findByBasicProfile_Name(invalidTargetName))
                    .willReturn(Optional.empty());

                FollowRequestDto requestDto = FollowRequestDto.builder()
                    .authUserRequestDto(authUserRequestDto)
                    .targetName(invalidTargetName)
                    .githubFollowing(false)
                    .build();

                // when, then
                assertThatCode(() -> userService.followUser(requestDto))
                    .isInstanceOf(InvalidUserException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("???????????? ?????? ???????????????.");

                verify(userRepository, times(1))
                    .findByBasicProfile_Name(loginUsername);
                verify(userRepository, times(1))
                    .findByBasicProfile_Name(invalidTargetName);
            }
        }

        @DisplayName("Source ????????? Target ????????? ???????????????")
        @Nested
        class Context_SourceAndTargetUserSame {

            @DisplayName("???????????? ??? ??????. - 400 ??????")
            @Test
            void follow_SameUser_400Exception() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserForUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));

                FollowRequestDto requestDto = FollowRequestDto.builder()
                    .authUserRequestDto(authUserRequestDto)
                    .targetName(loginUsername)
                    .githubFollowing(false)
                    .build();

                // when, then
                assertThatCode(() -> userService.followUser(requestDto))
                    .isInstanceOf(SameSourceTargetUserException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0004")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("?????? Source ??? Target ???????????????.");

                verify(userRepository, times(2))
                    .findByBasicProfile_Name(loginUsername);
            }

            @DisplayName("????????? ?????? ????????? ???????????????")
            @Nested
            class Context_githubFollowing {

                @DisplayName("true?????? ?????? ????????? ??????.")
                @Test
                void follow_githubFollowingTrue_AutoFollow() {
                    //given
                    User loginUser = UserFactory.user(1L, "testUser");
                    String loginUsername = loginUser.getName();
                    AuthUserForUserRequestDto authUserRequestDto =
                        createLoginAuthUserRequestDto(loginUsername);
                    User targetUser = UserFactory.user(2L, "testUser2");
                    String targetUsername = targetUser.getName();

                    given(userRepository.findByBasicProfile_Name(loginUsername))
                        .willReturn(Optional.of(loginUser));
                    given(userRepository.findByBasicProfile_Name(targetUsername))
                        .willReturn(Optional.of(targetUser));

                    FollowRequestDto requestDto = FollowRequestDto.builder()
                        .authUserRequestDto(authUserRequestDto)
                        .targetName(targetUsername)
                        .githubFollowing(true)
                        .build();

                    //when
                    FollowResponseDto responseDto =
                        userService.followUser(requestDto);

                    //then
                    assertThat(responseDto.getFollowerCount()).isEqualTo(1);
                    assertThat(responseDto.isFollowing()).isTrue();

                    verify(userRepository, times(1))
                        .findByBasicProfile_Name(loginUsername);
                    verify(userRepository, times(1))
                        .findByBasicProfile_Name(targetUsername);
                    verify(platformFollowingRequester, times(1))
                        .follow(targetUsername, authUserRequestDto.getAccessToken());
                }

                @DisplayName("false?????? ?????? ????????? ?????? ?????????")
                @Test
                void follow_githubFollowingFalse_NonAutoFollow() {
                    //given
                    User loginUser = UserFactory.user(1L, "testUser");
                    String loginUsername = loginUser.getName();
                    AuthUserForUserRequestDto authUserRequestDto =
                        createLoginAuthUserRequestDto(loginUsername);
                    User targetUser = UserFactory.user(2L, "testUser2");
                    String targetUsername = targetUser.getName();

                    given(userRepository.findByBasicProfile_Name(loginUsername))
                        .willReturn(Optional.of(loginUser));
                    given(userRepository.findByBasicProfile_Name(targetUsername))
                        .willReturn(Optional.of(targetUser));

                    FollowRequestDto requestDto = FollowRequestDto.builder()
                        .authUserRequestDto(authUserRequestDto)
                        .targetName(targetUsername)
                        .githubFollowing(false)
                        .build();

                    //when
                    FollowResponseDto responseDto =
                        userService.followUser(requestDto);

                    //then
                    assertThat(responseDto.getFollowerCount()).isEqualTo(1);
                    assertThat(responseDto.isFollowing()).isTrue();

                    verify(userRepository, times(1))
                        .findByBasicProfile_Name(loginUsername);
                    verify(userRepository, times(1))
                        .findByBasicProfile_Name(targetUsername);
                    verify(platformFollowingRequester, times(0))
                        .follow(targetUsername, authUserRequestDto.getAccessToken());
                }
            }
        }

        @DisplayName("Source ????????? ?????? Target ????????? ????????? ????????? ?????? ???")
        @Nested
        class Context_ValidOtherUser {

            @DisplayName("???????????? ??? ??????.")
            @Test
            void followUser_SourceToTarget_Success() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserForUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                User targetUser = UserFactory.user(2L, "testUser2");
                String targetUsername = targetUser.getName();

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));
                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.of(targetUser));

                FollowRequestDto requestDto = FollowRequestDto.builder()
                    .authUserRequestDto(authUserRequestDto)
                    .targetName(targetUsername)
                    .githubFollowing(false)
                    .build();

                //when
                FollowResponseDto responseDto =
                    userService.followUser(requestDto);

                //then
                assertThat(responseDto.getFollowerCount()).isEqualTo(1);
                assertThat(responseDto.isFollowing()).isTrue();

                verify(userRepository, times(1))
                    .findByBasicProfile_Name(loginUsername);
                verify(userRepository, times(1))
                    .findByBasicProfile_Name(targetUsername);
            }
        }

        @DisplayName("Source ????????? ?????? Target ????????? ?????? ????????? ????????????")
        @Nested
        class Context_AlreadyFollowingOtherUser {

            @DisplayName("????????? ??? ??? ??????.")
            @Test
            void followUser_ExistingFollow_400Exception() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserForUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                User targetUser = UserFactory.user(2L, "testUser2");
                String targetUsername = targetUser.getName();

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));
                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.of(targetUser));

                FollowRequestDto requestDto = FollowRequestDto.builder()
                    .authUserRequestDto(authUserRequestDto)
                    .targetName(targetUsername)
                    .githubFollowing(false)
                    .build();

                userService.followUser(requestDto);

                //when, then
                assertThatThrownBy(
                    () -> userService.followUser(requestDto)
                ).isInstanceOf(DuplicateFollowException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0002")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("?????? ????????? ??? ?????????.");

                verify(userRepository, times(2))
                    .findByBasicProfile_Name(loginUsername);
                verify(userRepository, times(2))
                    .findByBasicProfile_Name(targetUsername);
            }
        }
    }

    @DisplayName("unfollowUser ????????????")
    @Nested
    class Describe_unfollowUser {

        @DisplayName("?????????????????? ?????????")
        @Nested
        class Context_Guest {

            @DisplayName("??????????????? ??? ??????.")
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
        }

        @DisplayName("Target ????????? ???????????? ????????????")
        @Nested
        class Context_NotExistingOtherUser {

            @DisplayName("??????????????? ??? ??????. - 400 ??????")
            @Test
            void unfollow_FindByInvalidName_400Exception() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserForUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                String invalidTargetName = "django";

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));
                given(userRepository.findByBasicProfile_Name(invalidTargetName))
                    .willReturn(Optional.empty());

                FollowRequestDto requestDto = FollowRequestDto.builder()
                    .authUserRequestDto(authUserRequestDto)
                    .targetName(invalidTargetName)
                    .githubFollowing(false)
                    .build();

                // when, then
                assertThatCode(
                    () -> userService.unfollowUser(requestDto))
                    .isInstanceOf(InvalidUserException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("???????????? ?????? ???????????????.");

                verify(userRepository, times(1))
                    .findByBasicProfile_Name(loginUsername);
                verify(userRepository, times(1))
                    .findByBasicProfile_Name(invalidTargetName);
            }
        }

        @DisplayName("Source ????????? Target ????????? ???????????????")
        @Nested
        class Context_SourceAndTargetUserSame {

            @DisplayName("??????????????? ??? ??????. - 400 ??????")
            @Test
            void unfollow_SameUser_400Exception() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserForUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));

                FollowRequestDto requestDto = FollowRequestDto.builder()
                    .authUserRequestDto(authUserRequestDto)
                    .targetName(loginUsername)
                    .githubFollowing(false)
                    .build();

                // when, then
                assertThatCode(() -> userService.unfollowUser(requestDto))
                    .isInstanceOf(SameSourceTargetUserException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0004")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("?????? Source ??? Target ???????????????.");

                verify(userRepository, times(2))
                    .findByBasicProfile_Name(loginUsername);
            }
        }

        @DisplayName("Source ????????? ?????? Target ????????? ?????? ???????????? ????????????")
        @Nested
        class Context_InvalidOtherUser {

            @DisplayName("??????????????? ??? ??????. - 400 ??????")
            @Test
            void unfollowUser_NotExistingFollow_400Exception() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserForUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                User targetUser = UserFactory.user(2L, "testUser2");
                String targetUsername = targetUser.getName();

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));
                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.of(targetUser));

                FollowRequestDto requestDto = FollowRequestDto.builder()
                    .authUserRequestDto(authUserRequestDto)
                    .targetName(targetUsername)
                    .githubFollowing(false)
                    .build();

                //when
                assertThatThrownBy(
                    () -> userService.unfollowUser(requestDto)
                ).isInstanceOf(InvalidFollowException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0003")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("???????????? ?????? ????????? ?????????.");

                // then
                verify(userRepository, times(1))
                    .findByBasicProfile_Name(loginUsername);
                verify(userRepository, times(1))
                    .findByBasicProfile_Name(targetUsername);
            }
        }

        @DisplayName("Source ????????? ?????? Target ????????? ?????? ????????? ????????????")
        @Nested
        class Context_AlreadyFollowingOtherUser {

            @DisplayName("???????????? ??? ??? ??????.")
            @Test
            void unfollowUser_SourceToTarget_Success() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserForUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                User targetUser = UserFactory.user(2L, "testUser2");
                String targetUsername = targetUser.getName();

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));
                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.of(targetUser));

                FollowRequestDto requestDto = FollowRequestDto.builder()
                    .authUserRequestDto(authUserRequestDto)
                    .targetName(targetUsername)
                    .githubFollowing(false)
                    .build();

                userService.followUser(requestDto);

                //when
                FollowResponseDto responseDto =
                    userService.unfollowUser(requestDto);

                //then
                assertThat(responseDto.getFollowerCount()).isZero();
                assertThat(responseDto.isFollowing()).isFalse();

                verify(userRepository, times(2))
                    .findByBasicProfile_Name(loginUsername);
                verify(userRepository, times(2))
                    .findByBasicProfile_Name(targetUsername);
            }
        }

        @DisplayName("????????? ?????? ???????????? ???????????????")
        class Context_githubFollowing {

            @DisplayName("true?????? ?????? ????????? ??????.")
            @Test
            void unfollow_githubUnFollowingTrue_AutoUnFollow() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserForUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                User targetUser = UserFactory.user(2L, "testUser2");
                String targetUsername = targetUser.getName();

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));
                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.of(targetUser));

                FollowRequestDto requestDto = FollowRequestDto.builder()
                    .authUserRequestDto(authUserRequestDto)
                    .targetName(targetUsername)
                    .githubFollowing(true)
                    .build();

                userService.followUser(requestDto);

                //when
                FollowResponseDto responseDto =
                    userService.unfollowUser(requestDto);

                //then
                assertThat(responseDto.getFollowerCount()).isZero();
                assertThat(responseDto.isFollowing()).isFalse();

                verify(userRepository, times(2))
                    .findByBasicProfile_Name(loginUsername);
                verify(userRepository, times(2))
                    .findByBasicProfile_Name(targetUsername);
                verify(platformFollowingRequester, times(1))
                    .follow(targetUsername, authUserRequestDto.getAccessToken());
                verify(platformFollowingRequester, times(1))
                    .unfollow(targetUsername, authUserRequestDto.getAccessToken());
            }

            @DisplayName("false?????? ?????? ????????? ?????? ?????????.")
            @Test
            void unfollow_githubUnFollowingFalse_NonAutoUnFollow() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserForUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                User targetUser = UserFactory.user(2L, "testUser2");
                String targetUsername = targetUser.getName();

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));
                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.of(targetUser));

                FollowRequestDto requestDto = FollowRequestDto.builder()
                    .authUserRequestDto(authUserRequestDto)
                    .targetName(targetUsername)
                    .githubFollowing(false)
                    .build();

                userService.followUser(requestDto);

                //when
                FollowResponseDto responseDto =
                    userService.unfollowUser(requestDto);

                //then
                assertThat(responseDto.getFollowerCount()).isZero();
                assertThat(responseDto.isFollowing()).isFalse();

                verify(userRepository, times(2))
                    .findByBasicProfile_Name(loginUsername);
                verify(userRepository, times(2))
                    .findByBasicProfile_Name(targetUsername);
                verify(platformFollowingRequester, times(0))
                    .follow(targetUsername, authUserRequestDto.getAccessToken());
                verify(platformFollowingRequester, times(0))
                    .unfollow(targetUsername, authUserRequestDto.getAccessToken());
            }
        }
    }

    @DisplayName("editProfileImage ????????????")
    @Nested
    class Describe_editProfileImage {

        @DisplayName("????????? ????????? ???????????? ???????????? ?????????????????? URL??? ????????????.")
        @Test
        void editProfileImage_WithImage_Success() throws IOException {
            // given
            AuthUserForUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto(
                "testUser");
            File imageFile = FileFactory.getTestImage1File();
            byte[] imageSource = new FileInputStream(imageFile).readAllBytes();

            // mock
            given(userRepository.findByBasicProfile_Name("testUser"))
                .willReturn(Optional.of(UserFactory.user(1L, "testUser")));
            given(pickGitProfileStorage.storeByteFile(imageSource, "testUser"))
                .willReturn(imageFile.getName());

            // when
            ProfileImageEditRequestDto requestDto = ProfileImageEditRequestDto
                .builder()
                .image(imageSource)
                .build();
            ProfileImageEditResponseDto responseDto =
                userService.editProfileImage(authUserRequestDto, requestDto);

            // then
            assertThat(responseDto.getImageUrl()).isEqualTo(imageFile.getName());

            verify(userRepository, times(1))
                .findByBasicProfile_Name("testUser");
            verify(pickGitProfileStorage, times(1))
                .storeByteFile(imageSource, "testUser");
        }
    }

    @DisplayName("editProfileDescription ????????????")
    @Nested
    class Describe_editProfileDescription {

        @DisplayName("????????? ????????? ??? ??? ????????? ???????????? ??????????????????.")
        @Test
        void editProfileDescription_WithDescription_SuccesS() {
            // given
            AuthUserForUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto(
                "testUser");
            String description = "updated description";

            // mock
            given(userRepository.findByBasicProfile_Name("testUser"))
                .willReturn(Optional.of(UserFactory.user(1L, "testUser")));

            // when
            String updatedDescription = userService
                .editProfileDescription(authUserRequestDto, description);

            // then
            assertThat(updatedDescription).isEqualTo(description);
            verify(userRepository, times(1))
                .findByBasicProfile_Name("testUser");
        }
    }

    @DisplayName("????????? - ????????? ????????? ????????? ????????? ?????? ????????? ????????????. (???????????? ?????? boolean)")
    @Test
    void searchUser_LoginUser_Success() {
        // given
        String searchKeyword = "bing";
        int page = 0;
        int limit = 5;
        List<User> usersInDb = UserFactory.mockSearchUsersWithId();
        User loginUser = usersInDb.get(0);
        List<User> searchedUser = usersInDb.subList(1, usersInDb.size());
        AuthUserForUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto(
            loginUser.getName());

        // mock
        given(userSearchEngine.searchByUsernameLike(searchKeyword, PageRequest.of(page, limit)))
            .willReturn(searchedUser);
        given(userRepository.findByBasicProfile_Name(loginUser.getName()))
            .willReturn(Optional.ofNullable(loginUser));

        // when
        loginUser.follow(searchedUser.get(0));
        List<UserSearchResponseDto> searchResponses = userService
            .searchUser(authUserRequestDto, searchKeyword, PageRequest.of(0, 5));

        // then
        assertThat(searchResponses).hasSize(4);
        assertThat(searchResponses)
            .extracting("username")
            .containsExactly(searchedUser.stream().map(User::getName).toArray());
        assertThat(searchResponses)
            .extracting("following")
            .containsExactly(true, false, false, false);
        verify(userSearchEngine, times(1))
            .searchByUsernameLike(searchKeyword, PageRequest.of(page, limit));
        verify(userRepository, times(1)).findByBasicProfile_Name(loginUser.getName());
    }

    @DisplayName("??? ????????? - ????????? ????????? ????????? ????????? ?????? ????????? ????????????. (????????? ?????? null)")
    @Test
    void searchUser_GuestUser_Success() {
        // given
        String searchKeyword = "bing";
        int page = 0;
        int limit = 5;
        List<User> usersInDb = UserFactory.mockSearchUsersWithId();
        AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();

        // mock
        given(userSearchEngine.searchByUsernameLike(searchKeyword, PageRequest.of(page, limit)))
            .willReturn(usersInDb);

        // when
        List<UserSearchResponseDto> searchResult =
            userService.searchUser(authUserRequestDto, searchKeyword, PageRequest.of(0, 5));

        // then
        assertThat(searchResult).hasSize(5);
        assertThat(searchResult)
            .extracting("username")
            .containsExactly(usersInDb.stream().map(User::getName).toArray());
        assertThat(searchResult)
            .extracting("following")
            .containsExactly(null, null, null, null, null);
        verify(userSearchEngine, times(1))
            .searchByUsernameLike(searchKeyword, PageRequest.of(page, limit));
        verify(userRepository, times(0)).findByBasicProfile_Name(anyString());
    }

    @DisplayName("calculateContributions ????????????")
    @Nested
    class Describe_calculateContributions {

        @DisplayName("????????? ?????? ?????? ???")
        @Nested
        class Context_Login {

            @DisplayName("???????????? ?????? ????????? ????????? ??? ??????.")
            @Test
            void calculateContributions_LoginUser_Success() {
                // given
                User user = UserFactory.user();
                ContributionRequestDto requestDto = UserFactory.mockContributionRequestDto();

                Map<ContributionCategory, Integer> contributionMap = new EnumMap<>(ContributionCategory.class);
                contributionMap.put(ContributionCategory.STAR, 11);
                for (int i = 1; i < ContributionCategory.values().length; i++) {
                    contributionMap.put(ContributionCategory.values()[i], 48);
                }
                Contribution contribution = new Contribution(contributionMap);

                given(userRepository.findByBasicProfile_Name("testUser"))
                    .willReturn(Optional.of(user));
                given(platformContributionCalculator.calculate("oauth.access.token", "testUser"))
                    .willReturn(contribution);

                ContributionResponseDto responseDto = UserFactory.mockContributionResponseDto();

                // when
                ContributionResponseDto contributions = userService
                    .calculateContributions(requestDto);

                // then
                assertThat(contributions)
                    .usingRecursiveComparison()
                    .isEqualTo(responseDto);

                verify(userRepository, times(1))
                    .findByBasicProfile_Name("testUser");
                verify(platformContributionCalculator, times(1))
                    .calculate("oauth.access.token", "testUser");
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

                // then
                verify(userRepository, times(1))
                    .findByBasicProfile_Name("testUser");
            }
        }
    }

    @DisplayName("searchFollowings ????????????")
    @Nested
    class Describe_searchFollowings {

        @DisplayName("???????????? ???")
        @Nested
        class Context_Guest {

            @DisplayName("?????? ????????? ????????? ?????? ?????? ????????? ????????? ??? ??????. - ????????? ?????? null")
            @Test
            void searchFollowings_Guest_FollowingNull() {
                // given
                AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
                User target = UserFactory.user(1L, "target");
                List<User> followings = List.of(
                    UserFactory.user(2L, "ala"),
                    UserFactory.user(3L, "hello")
                );

                given(userRepository.findByBasicProfile_Name("target"))
                    .willReturn(Optional.of(target));
                given(userRepository.searchFollowingsOf(eq(target), eq(PageRequest.of(0, 3))))
                    .willReturn(followings);

                // when
                List<UserSearchResponseDto> response =
                    userService
                        .searchFollowings(authUserRequestDto, "target", PageRequest.of(0, 3));

                // then
                assertThat(response)
                    .extracting("username", "following")
                    .containsExactly(
                        tuple("ala", null),
                        tuple("hello", null)
                    );

                verify(userRepository, times(1)).findByBasicProfile_Name("target");
                verify(userRepository, times(1))
                    .searchFollowingsOf(eq(target), eq(PageRequest.of(0, 3)));
            }

            @DisplayName("???????????? ?????? ????????? ????????? ????????? ????????? ??? ??????.")
            @Test
            void searchFollowings_TargetNotExists_ExceptionThrown() {
                // given
                AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
                given(userRepository.findByBasicProfile_Name("target"))
                    .willReturn(Optional.empty());

                // when, then
                assertThatCode(() ->
                    userService
                        .searchFollowings(authUserRequestDto, "target", PageRequest.of(0, 10))
                ).isInstanceOf(InvalidUserException.class);

                // then
                verify(userRepository, times(1)).findByBasicProfile_Name("target");
            }
        }

        @DisplayName("????????? ????????? ???")
        @Nested
        class Context_LoginUser {

            @DisplayName("?????? ????????? ????????? ?????? ?????? ????????? ????????? ??? ??????. - ????????? ?????? true/false, ????????? null")
            @Test
            void searchFollowings_LoginUser_FollowingVarious() {
                // given
                AuthUserForUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto("source");
                User loginUser = UserFactory.user(4L, "source");
                User target = UserFactory.user(1L, "target");
                List<User> followings = List.of(
                    UserFactory.user(2L, "ala"),
                    UserFactory.user(3L, "hello"),
                    loginUser
                );
                loginUser.follow(followings.get(0));

                given(userRepository.findByBasicProfile_Name("target"))
                    .willReturn(Optional.of(target));
                given(userRepository.searchFollowingsOf(eq(target), eq(PageRequest.of(0, 3))))
                    .willReturn(followings);
                given(userRepository.findByBasicProfile_Name("source"))
                    .willReturn(Optional.of(loginUser));

                // when
                List<UserSearchResponseDto> response =
                    userService
                        .searchFollowings(authUserRequestDto, "target", PageRequest.of(0, 3));

                // then
                assertThat(response)
                    .extracting("username", "following")
                    .containsExactly(
                        tuple("ala", true),
                        tuple("hello", false),
                        tuple("source", null)
                    );

                verify(userRepository, times(1)).findByBasicProfile_Name("target");
                verify(userRepository, times(1))
                    .searchFollowingsOf(eq(target), eq(PageRequest.of(0, 3)));
                verify(userRepository, times(1)).findByBasicProfile_Name("source");
            }

            @DisplayName("???????????? ?????? ????????? ????????? ????????? ????????? ??? ??????.")
            @Test
            void searchFollowings_TargetNotExists_ExceptionThrown() {
                // given
                AuthUserForUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto(
                    "source");
                given(userRepository.findByBasicProfile_Name("target"))
                    .willReturn(Optional.empty());

                // when, then
                assertThatCode(() ->
                    userService
                        .searchFollowings(authUserRequestDto, "target", PageRequest.of(0, 10))
                ).isInstanceOf(InvalidUserException.class);

                // then
                verify(userRepository, times(1)).findByBasicProfile_Name("target");
            }
        }
    }

    @DisplayName("searchFollowers ????????????")
    @Nested
    class Describe_searchFollowers {

        @DisplayName("???????????? ???")
        @Nested
        class Context_Guest {

            @DisplayName("?????? ????????? ????????? ?????? ????????? ????????? ????????? ??? ??????. - ????????? ?????? null")
            @Test
            void searchFollowers_Guest_FollowingNull() {
                // given
                AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
                User target = UserFactory.user(1L, "target");
                List<User> followers = List.of(
                    UserFactory.user(2L, "ala"),
                    UserFactory.user(3L, "hello")
                );

                given(userRepository.findByBasicProfile_Name("target"))
                    .willReturn(Optional.of(target));
                given(userRepository.searchFollowersOf(eq(target), eq(PageRequest.of(0, 3))))
                    .willReturn(followers);

                // when
                List<UserSearchResponseDto> response =
                    userService
                        .searchFollowers(authUserRequestDto, "target", PageRequest.of(0, 3));

                // then
                assertThat(response)
                    .extracting("username", "following")
                    .containsExactly(
                        tuple("ala", null),
                        tuple("hello", null)
                    );

                verify(userRepository, times(1)).findByBasicProfile_Name("target");
                verify(userRepository, times(1))
                    .searchFollowersOf(eq(target), eq(PageRequest.of(0, 3)));
            }

            @DisplayName("???????????? ?????? ????????? ????????? ????????? ????????? ??? ??????.")
            @Test
            void searchFollowers_TargetNotExists_ExceptionThrown() {
                // given
                AuthUserForUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
                given(userRepository.findByBasicProfile_Name("target"))
                    .willReturn(Optional.empty());

                // when, then
                assertThatCode(() ->
                    userService.searchFollowers(authUserRequestDto, "target", PageRequest.of(0, 10))
                ).isInstanceOf(InvalidUserException.class);

                // then
                verify(userRepository, times(1)).findByBasicProfile_Name("target");
            }
        }

        @DisplayName("????????? ????????? ???")
        @Nested
        class Context_LoginUser {

            @DisplayName("?????? ????????? ????????? ?????? ????????? ????????? ????????? ??? ??????. - ????????? ?????? true/false, ????????? null")
            @Test
            void searchFollowers_LoginUser_FollowingVarious() {
                // given
                AuthUserForUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto(
                    "source");
                User loginUser = UserFactory.user(4L, "source");
                User target = UserFactory.user(1L, "target");
                List<User> followers = List.of(
                    UserFactory.user(2L, "ala"),
                    UserFactory.user(3L, "hello"),
                    loginUser
                );
                loginUser.follow(followers.get(0));

                given(userRepository.findByBasicProfile_Name("target"))
                    .willReturn(Optional.of(target));
                given(userRepository.searchFollowersOf(eq(target), eq(PageRequest.of(0, 3))))
                    .willReturn(followers);
                given(userRepository.findByBasicProfile_Name("source"))
                    .willReturn(Optional.of(loginUser));

                // when
                List<UserSearchResponseDto> response =
                    userService
                        .searchFollowers(authUserRequestDto, "target", PageRequest.of(0, 3));

                // then
                assertThat(response)
                    .extracting("username", "following")
                    .containsExactly(
                        tuple("ala", true),
                        tuple("hello", false),
                        tuple("source", null)
                    );

                verify(userRepository, times(1)).findByBasicProfile_Name("target");
                verify(userRepository, times(1))
                    .searchFollowersOf(eq(target), eq(PageRequest.of(0, 3)));
                verify(userRepository, times(1)).findByBasicProfile_Name("source");
            }

            @DisplayName("???????????? ?????? ????????? ????????? ????????? ????????? ??? ??????.")
            @Test
            void searchFollowers_TargetNotExists_ExceptionThrown() {
                // given
                AuthUserForUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto(
                    "source");

                given(userRepository.findByBasicProfile_Name("target"))
                    .willReturn(Optional.empty());

                // when, then
                assertThatCode(() ->
                    userService.searchFollowers(authUserRequestDto, "target", PageRequest.of(0, 10))
                ).isInstanceOf(InvalidUserException.class);

                // then
                verify(userRepository, times(1)).findByBasicProfile_Name("target");
            }
        }
    }

    private AuthUserForUserRequestDto createLoginAuthUserRequestDto(String username) {
        AppUser appUser = new LoginUser(username, "Bearer testToken");
        return UserAssembler.authUserForUserRequestDto(appUser);
    }

    private AuthUserForUserRequestDto createGuestAuthUserRequestDto() {
        return UserAssembler.authUserForUserRequestDto(new GuestUser());
    }
}
