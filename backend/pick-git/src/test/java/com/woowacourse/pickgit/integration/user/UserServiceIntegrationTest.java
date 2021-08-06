package com.woowacourse.pickgit.integration.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.user.DuplicateFollowException;
import com.woowacourse.pickgit.exception.user.InvalidFollowException;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.exception.user.SameSourceTargetUserException;
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
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.presentation.dto.request.ContributionRequestDto;
import java.util.List;
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

    @DisplayName("비로그인 유저는 내 프로필을 조회할 수 없다.")
    @Test
    void getMyUserProfile_Guest_Failure() {
        // given
        AuthUserRequestDto requestDto = createGuestAuthUserRequestDto();

        // when, then
        assertThatCode(() -> userService.getMyUserProfile(requestDto))
            .isInstanceOf(UnauthorizedException.class);
    }

    @DisplayName("로그인된 사용자는 자신의 프로필을 조회할 수 있다.")
    @Test
    void getMyUserProfile_WithMyName_Success() {
        //given
        User loginUser = userRepository.save(UserFactory.user());
        AuthUserRequestDto requestDto = createLoginAuthUserRequestDto(loginUser.getName());
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
        AuthUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
        UserProfileResponseDto responseDto = UserFactory.mockGuestUserProfileResponseDto();
        User targetUser = userRepository.save(UserFactory.user());

        //when
        UserProfileResponseDto userProfile =
            userService.getUserProfile(authUserRequestDto, targetUser.getName());

        //then
        assertThat(userProfile)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("게스트 유저는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_FindByInvalidNameInCaseOfGuestUser_400Exception() {
        // given
        AuthUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();

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
        AuthUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        userService.followUser(authUserRequestDto, target.getName());

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
        AuthUserRequestDto authUserRequestDto =
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

    @DisplayName("로그인 유저는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_FindByInvalidNameInCaseOfLoginUser_400Exception() {
        // given
        User loginUser = userRepository.save(UserFactory.user("testUser"));
        AuthUserRequestDto authUserRequestDto =
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
        AuthUserRequestDto requestDto = createGuestAuthUserRequestDto();

        // when, then
        assertThatCode(() -> userService.followUser(requestDto, "testUser"))
            .isInstanceOf(UnauthorizedException.class);
    }

    @DisplayName("로그인 유저는 존재하지 않는 유저에 대해 팔로우할 수 없다. - 400 예")
    @Test
    void follow_FindByInvalidName_400Exception() {
        // given
        User loginUser = userRepository.save(UserFactory.user("testUser"));
        AuthUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        // when, then
        assertThatCode(() -> userService.followUser(authUserRequestDto, "kevin"))
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
        AuthUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        // when, then
        assertThatCode(
            () -> userService.followUser(authUserRequestDto, loginUser.getName()))
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
        AuthUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        // when
        FollowResponseDto responseDto = userService
            .followUser(authUserRequestDto, target.getName());

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
        AuthUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        userService.followUser(authUserRequestDto, target.getName());

        // when
        assertThatThrownBy(() ->
            userService.followUser(authUserRequestDto, target.getName()))
            .isInstanceOf(DuplicateFollowException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("이미 팔로우 중 입니다.");
    }

    @DisplayName("비로그인 유저는 언팔로우할 수 없다.")
    @Test
    void unfollow_Guest_Failure() {
        // given
        AuthUserRequestDto requestDto = createGuestAuthUserRequestDto();

        // when, then
        assertThatCode(() -> userService.unfollowUser(requestDto, "testUser"))
            .isInstanceOf(UnauthorizedException.class);
    }

    @DisplayName("로그인 유저는 존재하지 않는 유저에 대해 언팔로우할 수 없다. - 400 예외")
    @Test
    void unfollow_FindByInvalidName_400Exception() {
        //given
        User loginUser = userRepository.save(UserFactory.user("testUser"));
        AuthUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        // when, then
        assertThatCode(() -> userService.followUser(authUserRequestDto, "kevin"))
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
        AuthUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        // when, then
        assertThatCode(
            () -> userService.unfollowUser(authUserRequestDto, loginUser.getName()))
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
        AuthUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        // when, then
        assertThatThrownBy(
            () -> userService.unfollowUser(authUserRequestDto, target.getName()))
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
        AuthUserRequestDto authUserRequestDto =
            createLoginAuthUserRequestDto(loginUser.getName());

        userService.followUser(authUserRequestDto, target.getName());

        // when
        FollowResponseDto responseDto = userService
            .unfollowUser(authUserRequestDto, target.getName());

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

    @DisplayName("자신의 프로필(이미지, 한 줄 소개 포함)을 수정할 수 있다.")
    @Test
    void editUserProfile_WithImageAndDescription_Success() {
        // given
        String updatedDescription = "updated description";
        User user = UserFactory.user("testUser");
        AuthUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto("testUser");

        userRepository.save(user);

        // when
        ProfileEditRequestDto profileEditRequestDto = ProfileEditRequestDto
            .builder()
            .image(FileFactory.getTestImage1())
            .decription(updatedDescription)
            .build();
        ProfileEditResponseDto responseDto =
            userService.editProfile(authUserRequestDto, profileEditRequestDto);

        // then
        assertThat(responseDto.getImageUrl()).isNotBlank();
        assertThat(responseDto.getDescription()).isEqualTo(updatedDescription);
    }

    @DisplayName("자신의 프로필(한 줄 소개만 포함)을 수정할 수 있다.")
    @Test
    void editUserProfile_WithDescription_Success() {
        // given
        String updatedDescription = "updated description";
        User user = UserFactory.user("testUser");
        AuthUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto("testUser");

        userRepository.save(user);

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
    }

    @DisplayName("로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색한다. 단, 자기 자신은 검색되지 않는다. (팔로잉한 여부 boolean)")
    @Test
    void searchUser_LoginUser_Success() {
        // given
        String searchKeyword = "bing";
        UserSearchRequestDto userSearchRequestDto = UserSearchRequestDto
            .builder()
            .keyword(searchKeyword)
            .page(0L)
            .limit(5L)
            .build();
        List<User> usersInDb = UserFactory.mockSearchUsers();
        User loginUser = usersInDb.get(0);
        List<User> searchedUsers = usersInDb.subList(1, usersInDb.size());
        AuthUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto(loginUser.getName());

        userRepository.save(loginUser);
        searchedUsers.forEach(user -> userRepository.save(user));

        // when
        userService.followUser(authUserRequestDto, searchedUsers.get(0).getName());

        List<UserSearchResponseDto> searchResult =
            userService.searchUser(authUserRequestDto, userSearchRequestDto);

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
        UserSearchRequestDto userSearchRequestDto = UserSearchRequestDto
            .builder()
            .keyword(searchKeyword)
            .page(0L)
            .limit(3L)
            .build();
        AuthUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
        List<User> userInDb = UserFactory.mockSearchUsers();
        userRepository.saveAll(userInDb);

        // when
        List<UserSearchResponseDto> searchResult =
            userService.searchUser(authUserRequestDto, userSearchRequestDto);

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
        AuthUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto("bingbing");
        FollowSearchRequestDto followSearchRequestDto =
            FollowSearchRequestDto.builder()
                .username("target")
                .page(0L)
                .limit(10L)
                .build();
        List<User> usersInDb = UserFactory.mockSearchUsers();
        User targetUser = UserFactory.user("target");

        userRepository.saveAll(usersInDb);
        userRepository.save(targetUser);

        usersInDb.forEach(mockUser -> {
            AuthUserRequestDto targetAuthDto = createLoginAuthUserRequestDto("target");
            userService.followUser(targetAuthDto, mockUser.getName());
        });
        for (int i = 0; i < 3; i++) {
            userService.followUser(authUserRequestDto, usersInDb.get(i).getName());
        }

        // when
        List<UserSearchResponseDto> response =
            userService.searchFollowings(authUserRequestDto, followSearchRequestDto);

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
        AuthUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
        FollowSearchRequestDto followSearchRequestDto =
            FollowSearchRequestDto.builder()
                .username("target")
                .page(0L)
                .limit(10L)
                .build();
        List<User> usersInDb = UserFactory.mockSearchUsers();
        User targetUser = UserFactory.user("target");

        userRepository.saveAll(usersInDb);
        userRepository.save(targetUser);

        usersInDb.forEach(mockUser -> {
            AuthUserRequestDto targetAuthDto = createLoginAuthUserRequestDto("target");
            userService.followUser(targetAuthDto, mockUser.getName());
        });

        // when
        List<UserSearchResponseDto> response =
            userService.searchFollowings(authUserRequestDto, followSearchRequestDto);

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
        AuthUserRequestDto authUserRequestDto = createLoginAuthUserRequestDto("bingbing");
        FollowSearchRequestDto followSearchRequestDto =
            FollowSearchRequestDto.builder()
                .username("target")
                .page(0L)
                .limit(10L)
                .build();
        List<User> usersInDb = UserFactory.mockSearchUsers();
        User targetUser = UserFactory.user("target");

        userRepository.saveAll(usersInDb);
        userRepository.save(targetUser);

        usersInDb.forEach(mockUser -> {
            AuthUserRequestDto mockUserAuthDto = createLoginAuthUserRequestDto(mockUser.getName());
            userService.followUser(mockUserAuthDto, "target");
        });
        for (int i = 0; i < 3; i++) {
            userService.followUser(authUserRequestDto, usersInDb.get(i).getName());
        }

        // when
        List<UserSearchResponseDto> response =
            userService.searchFollowers(authUserRequestDto, followSearchRequestDto);

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
        AuthUserRequestDto authUserRequestDto = createGuestAuthUserRequestDto();
        FollowSearchRequestDto followSearchRequestDto =
            FollowSearchRequestDto.builder()
                .username("target")
                .page(0L)
                .limit(10L)
                .build();
        List<User> usersInDb = UserFactory.mockSearchUsers();
        User targetUser = UserFactory.user("target");

        userRepository.saveAll(usersInDb);
        userRepository.save(targetUser);

        usersInDb.forEach(mockUser -> {
            AuthUserRequestDto mockUserAuthDto = createLoginAuthUserRequestDto(mockUser.getName());
            userService.followUser(mockUserAuthDto, "target");
        });

        // when
        List<UserSearchResponseDto> response =
            userService.searchFollowers(authUserRequestDto, followSearchRequestDto);

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

    private AuthUserRequestDto createLoginAuthUserRequestDto(String username) {
        AppUser appUser = new LoginUser(username, "Bearer testToken");
        return AuthUserRequestDto.from(appUser);
    }

    private AuthUserRequestDto createGuestAuthUserRequestDto() {
        return AuthUserRequestDto.from(new GuestUser());
    }
}
