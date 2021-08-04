package com.woowacourse.pickgit.unit.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.user.DuplicateFollowException;
import com.woowacourse.pickgit.exception.user.InvalidFollowException;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.exception.user.SameSourceTargetUserException;
import com.woowacourse.pickgit.post.domain.repository.PickGitStorage;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.UserSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.domain.Contribution;
import com.woowacourse.pickgit.user.domain.PlatformContributionCalculator;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.presentation.dto.request.ContributionRequestDto;
import java.io.File;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PickGitStorage pickGitStorage;

    @Mock
    private PlatformContributionCalculator platformContributionCalculator;

    @DisplayName("getMyUserProfile 메서드는")
    @Nested
    class Describe_getMyUserProfile {

        @DisplayName("로그인 되어있을 때")
        @Nested
        class Context_Login {

            @DisplayName("사용자는 내 프로필을 조회할 수 있다.")
            @Test
            void getMyUserProfile_WithMyName_Success() {
                // given
                User loginUser = UserFactory.user();
                String username = loginUser.getName();
                AuthUserRequestDto requestDto = createLoginAuthUserRequestDto(username);

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

        @DisplayName("비로그인되어 있으면")
        @Nested
        class Context_Guest {

            @DisplayName("사용자는 내 프로필을 조회할 수 없다.")
            @Test
            void getMyUserProfile_Guest_Failure() {
                // given
                AuthUserRequestDto requestDto = createGuestAuthUserRequestDto();

                // when, then
                assertThatCode(() -> userService.getMyUserProfile(requestDto))
                    .isInstanceOf(UnauthorizedException.class);
            }
        }
    }

    @DisplayName("getUserProfile 메서드는")
    @Nested
    class Describe_getUserProfile {

        @DisplayName("게스트 유저일 때")
        @Nested
        class Context_GuestUser {

            @DisplayName("유저 이름으로 검색하여 유저의 프로필을 조회할 수 있다.")
            @Test
            void getUserProfile_FindByNameInCaseOfGuestUser_Success() {
                //given
                AuthUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
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

            @DisplayName("존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
            @Test
            void getUserProfile_FindByInvalidNameInCaseOfGuestUser_400Exception() {
                //given
                AuthUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
                String invalidName = "InvalidName";

                given(userRepository.findByBasicProfile_Name(invalidName))
                    .willReturn(Optional.empty());

                //when
                assertThatThrownBy(
                    () -> userService.getUserProfile(authUserRequestDto, invalidName)
                ).isInstanceOf(InvalidUserException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("유효하지 않은 유저입니다.");

                // then
                verify(userRepository, times(1))
                    .findByBasicProfile_Name(invalidName);
            }
        }

        @DisplayName("로그인 유저일 때")
        @Nested
        class Context_LoginUser {

            @DisplayName("팔로잉 중인 유저의 프로필을 조회할 수 있다.")
            @Test
            void getUserProfile_FindByNameInCaseOfLoginUserIsFollowing_Success() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                User targetUser = UserFactory.user(2L, "testUser2");
                String targetUsername = targetUser.getName();

                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.of(targetUser));
                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));

                UserProfileResponseDto responseDto =
                    UserFactory.mockLoginUserProfileIsFollowingResponseDto();

                userService.followUser(authUserRequestDto, targetUsername);

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

            @DisplayName("팔로잉하지 않은 유저의 프로필을 조회할 수 있다.")
            @Test
            void getUserProfile_FindByNameInCaseOfLoginUseIsNotFollowing_Success() {
                //given
                User loginUser = UserFactory.user("testUser");
                String loginUsername = loginUser.getName();
                AuthUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                User targetUser = UserFactory.user("testUser2");
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

            @DisplayName("존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
            @Test
            void getUserProfile_FindByInvalidNameInCaseOfLoginUser_400Exception() {
                //given
                User loginUser = UserFactory.user("testUser");
                String loginUsername = loginUser.getName();
                AuthUserRequestDto authUserRequestDto =
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
                    .hasMessage("유효하지 않은 유저입니다.");

                verify(userRepository, times(1))
                    .findByBasicProfile_Name(targetUsername);
                verify(userRepository, times(0))
                    .findByBasicProfile_Name(loginUsername);
            }
        }
    }

    @DisplayName("followUser 메서드는")
    @Nested
    class Describe_followUser {

        @DisplayName("비로그인되어 있으면")
        @Nested
        class Context_Guest {

            @DisplayName("팔로우할 수 없다.")
            @Test
            void follow_Guest_Failure() {
                // given
                AuthUserRequestDto requestDto = createGuestAuthUserRequestDto();

                // when, then
                assertThatCode(() -> userService.followUser(requestDto, "testUser"))
                    .isInstanceOf(UnauthorizedException.class);
            }
        }

        @DisplayName("Target 유저가 존재하지 않는다면")
        @Nested
        class Context_NotExistingOtherUser {

            @DisplayName("팔로우할 수 없다. - 400 예외")
            @Test
            void follow_FindByInvalidName_400Exception() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                String invalidTargetName = "django";

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));
                given(userRepository.findByBasicProfile_Name(invalidTargetName))
                    .willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> userService.followUser(authUserRequestDto, invalidTargetName))
                    .isInstanceOf(InvalidUserException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("유효하지 않은 유저입니다.");

                verify(userRepository, times(1))
                    .findByBasicProfile_Name(loginUsername);
                verify(userRepository, times(1))
                    .findByBasicProfile_Name(invalidTargetName);
            }
        }

        @DisplayName("Source 유저와 Target 유저가 동일하다면")
        @Nested
        class Context_SourceAndTargetUserSame {

            @DisplayName("팔로우할 수 없다. - 400 예외")
            @Test
            void follow_SameUser_400Exception() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));

                // when, then
                assertThatCode(() -> userService.followUser(authUserRequestDto, loginUsername))
                    .isInstanceOf(SameSourceTargetUserException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0004")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("같은 Source 와 Target 유저입니다.");

                verify(userRepository, times(2))
                    .findByBasicProfile_Name(loginUsername);
            }
        }

        @DisplayName("Source 유저가 특정 Target 유저를 팔로우 중이지 않을 때")
        @Nested
        class Context_ValidOtherUser {

            @DisplayName("팔로우할 수 있다.")
            @Test
            void followUser_SourceToTarget_Success() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                User targetUser = UserFactory.user(2L, "testUser2");
                String targetUsername = targetUser.getName();

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));
                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.of(targetUser));

                //when
                FollowResponseDto responseDto =
                    userService.followUser(authUserRequestDto, targetUsername);

                //then
                assertThat(responseDto.getFollowerCount()).isEqualTo(1);
                assertThat(responseDto.isFollowing()).isTrue();

                verify(userRepository, times(1))
                    .findByBasicProfile_Name(loginUsername);
                verify(userRepository, times(1))
                    .findByBasicProfile_Name(targetUsername);
            }
        }

        @DisplayName("Source 유저가 특정 Target 유저를 이미 팔로우 중이라면")
        @Nested
        class Context_AlreadyFollowingOtherUser {

            @DisplayName("팔로우 할 수 없다.")
            @Test
            void followUser_ExistingFollow_400Exception() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                User targetUser = UserFactory.user(2L, "testUser2");
                String targetUsername = targetUser.getName();

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));
                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.of(targetUser));

                userService.followUser(authUserRequestDto, targetUsername);

                //when, then
                assertThatThrownBy(
                    () -> userService.followUser(authUserRequestDto, targetUsername)
                ).isInstanceOf(DuplicateFollowException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0002")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("이미 팔로우 중 입니다.");

                verify(userRepository, times(2))
                    .findByBasicProfile_Name(loginUsername);
                verify(userRepository, times(2))
                    .findByBasicProfile_Name(targetUsername);
            }
        }
    }

    @DisplayName("unfollowUser 메서드는")
    @Nested
    class Describe_unfollowUser {

        @DisplayName("비로그인되어 있으면")
        @Nested
        class Context_Guest {

            @DisplayName("언팔로우할 수 없다.")
            @Test
            void unfollow_Guest_Failure() {
                // given
                AuthUserRequestDto requestDto = createGuestAuthUserRequestDto();

                // when, then
                assertThatCode(() -> userService.unfollowUser(requestDto, "testUser"))
                    .isInstanceOf(UnauthorizedException.class);
            }
        }

        @DisplayName("Target 유저가 존재하지 않는다면")
        @Nested
        class Context_NotExistingOtherUser {

            @DisplayName("언팔로우할 수 없다. - 400 예외")
            @Test
            void unfollow_FindByInvalidName_400Exception() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                String invalidTargetName = "django";

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));
                given(userRepository.findByBasicProfile_Name(invalidTargetName))
                    .willReturn(Optional.empty());

                // when, then
                assertThatCode(
                    () -> userService.unfollowUser(authUserRequestDto, invalidTargetName))
                    .isInstanceOf(InvalidUserException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("유효하지 않은 유저입니다.");

                verify(userRepository, times(1))
                    .findByBasicProfile_Name(loginUsername);
                verify(userRepository, times(1))
                    .findByBasicProfile_Name(invalidTargetName);
            }
        }

        @DisplayName("Source 유저와 Target 유저가 동일하다면")
        @Nested
        class Context_SourceAndTargetUserSame {

            @DisplayName("언팔로우할 수 없다. - 400 예외")
            @Test
            void unfollow_SameUser_400Exception() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));

                // when, then
                assertThatCode(() -> userService.unfollowUser(authUserRequestDto, loginUsername))
                    .isInstanceOf(SameSourceTargetUserException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0004")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("같은 Source 와 Target 유저입니다.");

                verify(userRepository, times(2))
                    .findByBasicProfile_Name(loginUsername);
            }
        }

        @DisplayName("Source 유저가 특정 Target 유저를 이미 언팔로우 중이라면")
        @Nested
        class Context_InvalidOtherUser {

            @DisplayName("언팔로우할 수 없다. - 400 예외")
            @Test
            void unfollowUser_NotExistingFollow_400Exception() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                User targetUser = UserFactory.user(2L, "testUser2");
                String targetUsername = targetUser.getName();

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));
                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.of(targetUser));

                //when
                assertThatThrownBy(
                    () -> userService.unfollowUser(authUserRequestDto, targetUsername)
                ).isInstanceOf(InvalidFollowException.class)
                    .hasFieldOrPropertyWithValue("errorCode", "U0003")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("존재하지 않는 팔로우 입니다.");

                // then
                verify(userRepository, times(1))
                    .findByBasicProfile_Name(loginUsername);
                verify(userRepository, times(1))
                    .findByBasicProfile_Name(targetUsername);
            }
        }

        @DisplayName("Source 유저가 특정 Target 유저를 이미 팔로우 중이라면")
        @Nested
        class Context_AlreadyFollowingOtherUser {

            @DisplayName("언팔로우 할 수 있다.")
            @Test
            void unfollowUser_SourceToTarget_Success() {
                //given
                User loginUser = UserFactory.user(1L, "testUser");
                String loginUsername = loginUser.getName();
                AuthUserRequestDto authUserRequestDto =
                    createLoginAuthUserRequestDto(loginUsername);
                User targetUser = UserFactory.user(2L, "testUser2");
                String targetUsername = targetUser.getName();

                given(userRepository.findByBasicProfile_Name(loginUsername))
                    .willReturn(Optional.of(loginUser));
                given(userRepository.findByBasicProfile_Name(targetUsername))
                    .willReturn(Optional.of(targetUser));

                userService.followUser(authUserRequestDto, targetUsername);

                //when
                FollowResponseDto responseDto =
                    userService.unfollowUser(authUserRequestDto, targetUsername);

                //then
                assertThat(responseDto.getFollowerCount()).isZero();
                assertThat(responseDto.isFollowing()).isFalse();

                verify(userRepository, times(2))
                    .findByBasicProfile_Name(loginUsername);
                verify(userRepository, times(2))
                    .findByBasicProfile_Name(targetUsername);
            }
        }
    }

    @DisplayName("자신의 프로필(이미지, 한 줄 소개 포함)을 수정할 수 있다.")
    @Test
    void editUserProfile_WithImageAndDescription_Success() {
        // given
        MultipartFile image = FileFactory.getTestImage1();
        String updatedDescription = "updated description";
        AuthUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto("testUser");

        // mock
        given(userRepository.findByBasicProfile_Name("testUser"))
            .willReturn(Optional.of(UserFactory.user(1L, "testUser")));
        given(pickGitStorage.store(any(File.class), anyString()))
            .willReturn(Optional.ofNullable(image.getName()));

        // when
        ProfileEditRequestDto profileEditRequestDto = ProfileEditRequestDto
            .builder()
            .image(image)
            .decription(updatedDescription)
            .build();
        ProfileEditResponseDto responseDto =
            userService.editProfile(authUserRequestDto, profileEditRequestDto);

        // then
        assertThat(responseDto.getImageUrl()).isEqualTo(image.getName());
        assertThat(responseDto.getDescription()).isEqualTo(updatedDescription);
        verify(userRepository, times(1))
            .findByBasicProfile_Name("testUser");
        verify(pickGitStorage, times(1))
            .store(any(File.class), anyString());
    }

    @DisplayName("자신의 프로필(한 줄 소개만 포함)을 수정할 수 있다.")
    @Test
    void editUserProfile_WithDescription_Success() {
        // given
        User user = UserFactory.user(1L, "testUser");
        String updatedDescription = "updated descrption";
        AuthUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto("testUser");

        // mock
        given(userRepository.findByBasicProfile_Name("testUser"))
            .willReturn(Optional.of(user));

        // when
        ProfileEditRequestDto profileEditRequestDto = ProfileEditRequestDto
            .builder()
            .image(FileFactory.getEmptyTestFile())
            .decription(updatedDescription)
            .build();
        ProfileEditResponseDto responseDto =
            userService.editProfile(authUserRequestDto, profileEditRequestDto);

        // then
        assertThat(responseDto.getImageUrl()).isEqualTo(user.getImage());
        assertThat(responseDto.getDescription()).isEqualTo(updatedDescription);
        verify(userRepository, times(1))
            .findByBasicProfile_Name(user.getName());
    }

    @DisplayName("로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색한다. (팔로잉한 여부 boolean)")
    @Test
    void searchUser_LoginUser_Success() {
        // given
        String searchKeyword = "bing";
        int page = 0;
        int limit = 5;
        List<User> usersInDb = UserFactory.mockSearchUsersWithId();
        User loginUser = usersInDb.get(0);
        List<User> searchedUser = usersInDb.subList(1, usersInDb.size());
        UserSearchRequestDto userSearchRequestDto = UserSearchRequestDto
            .builder()
            .keyword(searchKeyword)
            .page(0L)
            .limit(5L)
            .build();
        AuthUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto(loginUser.getName());

        // mock
        given(userRepository.searchByUsernameLike(searchKeyword, PageRequest.of(page, limit)))
            .willReturn(searchedUser);
        given(userRepository.findByBasicProfile_Name(loginUser.getName()))
            .willReturn(Optional.ofNullable(loginUser));

        // when
        loginUser.follow(searchedUser.get(0));
        List<UserSearchResponseDto> searchResponses = userService
            .searchUser(authUserRequestDto, userSearchRequestDto);

        // then
        assertThat(searchResponses).hasSize(4);
        assertThat(searchResponses)
            .extracting("username")
            .containsExactly(searchedUser.stream().map(User::getName).toArray());
        assertThat(searchResponses)
            .extracting("following")
            .containsExactly(true, false, false, false);
        verify(userRepository, times(1))
            .searchByUsernameLike(searchKeyword, PageRequest.of(page, limit));
        verify(userRepository, times(1)).findByBasicProfile_Name(loginUser.getName());
    }

    @DisplayName("비 로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색한다. (팔로잉 필드 null)")
    @Test
    void searchUser_GuestUser_Success() {
        // given
        String searchKeyword = "bing";
        int page = 0;
        int limit = 5;
        List<User> usersInDb = UserFactory.mockSearchUsersWithId();
        UserSearchRequestDto userSearchRequestDto = UserSearchRequestDto
            .builder()
            .keyword(searchKeyword)
            .page(0L)
            .limit(5L)
            .build();
        AuthUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();

        // mock
        given(userRepository.searchByUsernameLike(searchKeyword, PageRequest.of(page, limit)))
            .willReturn(usersInDb);

        // when
        List<UserSearchResponseDto> searchResult =
            userService.searchUser(authUserRequestDto, userSearchRequestDto);

        // then
        assertThat(searchResult).hasSize(5);
        assertThat(searchResult)
            .extracting("username")
            .containsExactly(usersInDb.stream().map(User::getName).toArray());
        assertThat(searchResult)
            .extracting("following")
            .containsExactly(null, null, null, null, null);
        verify(userRepository, times(1))
            .searchByUsernameLike(searchKeyword, PageRequest.of(page, limit));
        verify(userRepository, times(0)).findByBasicProfile_Name(anyString());
    }

    @DisplayName("calculateContributions 메소드는")
    @Nested
    class Describe_calculateContributions {

        @DisplayName("로그인 되어 있을 때")
        @Nested
        class Context_Login {

            @DisplayName("사용자는 활동 통계를 조회할 수 있다.")
            @Test
            void calculateContributions_LoginUser_Success() {
                // given
                User user = UserFactory.user();
                ContributionRequestDto requestDto = UserFactory.mockContributionRequestDto();

                Contribution contribution = new Contribution(11, 48, 48, 48, 48);

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

            @DisplayName("존재하지 않는 유저 이름으로 활동 통계를 조회할 수 없다. - 400 예외")
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

                // then
                verify(userRepository, times(1))
                    .findByBasicProfile_Name("testUser");
            }
        }
    }

    @DisplayName("searchFollowings 메서드는")
    @Nested
    class Describe_searchFollowings {

        @DisplayName("게스트일 때")
        @Nested
        class Context_Guest {

            @DisplayName("특정 유저가 팔로잉 중인 유저 목록을 조회할 수 있다. - 팔로우 여부 null")
            @Test
            void searchFollowings_Guest_FollowingNull() {
                // given
                AuthUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
                FollowSearchRequestDto followSearchRequestDto =
                    FollowSearchRequestDto.builder()
                        .username("target")
                        .page(0L)
                        .limit(3L)
                        .build();
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
                    userService.searchFollowings(authUserRequestDto, followSearchRequestDto);

                // then
                assertThat(response)
                    .extracting("username")
                    .containsExactly("ala", "hello");

                assertThat(response)
                    .extracting("following")
                    .containsOnlyNulls();

                verify(userRepository, times(1)).findByBasicProfile_Name("target");
                verify(userRepository, times(1))
                    .searchFollowingsOf(eq(target), eq(PageRequest.of(0, 3)));
            }

            @DisplayName("존재하지 않는 유저의 팔로잉 목록을 조회할 수 없다.")
            @Test
            void searchFollowings_TargetNotExists_ExceptionThrown() {
                // given
                AuthUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
                FollowSearchRequestDto followSearchRequestDto =
                    FollowSearchRequestDto.builder()
                        .username("target")
                        .page(0L)
                        .limit(3L)
                        .build();

                given(userRepository.findByBasicProfile_Name("target"))
                    .willReturn(Optional.empty());

                // when, then
                assertThatCode(() ->
                    userService.searchFollowings(authUserRequestDto, followSearchRequestDto)
                ).isInstanceOf(InvalidUserException.class);

                // then
                verify(userRepository, times(1)).findByBasicProfile_Name("target");
            }
        }

        @DisplayName("로그인 유저일 때")
        @Nested
        class Context_LoginUser {

            @DisplayName("특정 유저가 팔로잉 중인 유저 목록을 조회할 수 있다. - 팔로우 여부 true/false, 본인은 null")
            @Test
            void searchFollowings_LoginUser_FollowingNull() {
                // given
                AuthUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto("source");
                FollowSearchRequestDto followSearchRequestDto =
                    FollowSearchRequestDto.builder()
                        .username("target")
                        .page(0L)
                        .limit(3L)
                        .build();
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
                    userService.searchFollowings(authUserRequestDto, followSearchRequestDto);

                // then
                assertThat(response)
                    .extracting("username")
                    .containsExactly("ala", "hello", "source");

                assertThat(response)
                    .extracting("following")
                    .containsExactly(true, false, null);

                verify(userRepository, times(1)).findByBasicProfile_Name("target");
                verify(userRepository, times(1))
                    .searchFollowingsOf(eq(target), eq(PageRequest.of(0, 3)));
                verify(userRepository, times(1)).findByBasicProfile_Name("source");
            }

            @DisplayName("존재하지 않는 유저의 팔로잉 목록을 조회할 수 없다.")
            @Test
            void searchFollowings_TargetNotExists_ExceptionThrown() {
                // given
                AuthUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto("source");
                FollowSearchRequestDto followSearchRequestDto =
                    FollowSearchRequestDto.builder()
                        .username("target")
                        .page(0L)
                        .limit(3L)
                        .build();

                given(userRepository.findByBasicProfile_Name("target"))
                    .willReturn(Optional.empty());

                // when, then
                assertThatCode(() ->
                    userService.searchFollowings(authUserRequestDto, followSearchRequestDto)
                ).isInstanceOf(InvalidUserException.class);

                // then
                verify(userRepository, times(1)).findByBasicProfile_Name("target");
            }
        }
    }

    private AuthUserRequestDto createLoginAuthUserRequestDto(String username) {
        AppUser appUser = new LoginUser(username, "Bearer testToken");
        return AuthUserRequestDto.from(appUser);
    }

    private AuthUserRequestDto createGuestAuthUserRequestDto() {
        return AuthUserRequestDto.from(new GuestUser());
    }
}
