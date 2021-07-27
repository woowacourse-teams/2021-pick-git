package com.woowacourse.pickgit.integration.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.exception.user.DuplicateFollowException;
import com.woowacourse.pickgit.exception.user.InvalidFollowException;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.post.domain.PickGitStorage;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.UserSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
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

    @Autowired
    private PickGitStorage pickGitStorage;

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
        AuthUserRequestDto authUserRequestDto =
            new AuthUserRequestDto(guestUser.getUsername2(), guestUser.isGuest());
        UserProfileResponseDto responseDto = UserFactory.mockGuestUserProfileResponseDto();

        userRepository.save(UserFactory.user());

        //when
        UserProfileResponseDto userProfile =
            userService.getUserProfile(authUserRequestDto, "testUser");

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
        AuthUserRequestDto authUserRequestDto =
            new AuthUserRequestDto(loginUser.getUsername(), loginUser.isGuest());

        User source = userRepository.save(UserFactory.user("testUser"));
        User target = userRepository.save(UserFactory.user("testUser2"));

        AuthUserRequestDto requestDto = new AuthUserRequestDto(source.getName());
        userService.followUser(requestDto, target.getName());

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

    @DisplayName("사용자는 유저 이름으로 검색하여 유저의 프로필을 조회할 수 있다. - 팔로잉을 하지 않은 경우")
    @Test
    void getUserProfile_FindByNameInCaseOfLoginUserIsNotFollowing_Success() {
        // given
        AppUser loginUser = new LoginUser("testUser", "Bearer testToken");
        AuthUserRequestDto authUserRequestDto =
            new AuthUserRequestDto(loginUser.getUsername(), loginUser.isGuest());

        userRepository.save(UserFactory.user("testUser"));
        userRepository.save(UserFactory.user("testUser2"));

        UserProfileResponseDto responseDto =
            UserFactory.mockLoginUserProfileIsNotFollowingResponseDto();

        // when
        UserProfileResponseDto userProfile = userService.getUserProfile(authUserRequestDto, "testUser2");

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
        AuthUserRequestDto authUserRequestDto =
            new AuthUserRequestDto(guestUser.getUsername2(), guestUser.isGuest());

        // when
        assertThatThrownBy(() -> {
            userService.getUserProfile(authUserRequestDto, "invalidName");
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
        AuthUserRequestDto authUserRequestDto =
            new AuthUserRequestDto(guestUser.getUsername2(), guestUser.isGuest());

        // when
        assertThatThrownBy(() -> {
            userService.getUserProfile(authUserRequestDto, "invalidName");
        }).isInstanceOf(InvalidUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("유효하지 않은 유저입니다.");
    }

    @DisplayName("누구든지 활동 통계를 조회할 수 있다.")
    @Test
    void getContributions_Anyone_Success() {
        // given
        userRepository.save(UserFactory.user());

        ContributionResponseDto contributions = UserFactory.mockContributionResponseDto();

        // when
        ContributionResponseDto responseDto = userService.calculateContributions("testUser");

        // then
        assertThat(responseDto)
            .usingRecursiveComparison()
            .isEqualTo(contributions);
    }

    @DisplayName("존재하지 않은 유저 이름으로 활동 통계를 조회할 수 없다. - 400 예외")
    @Test
    void getContributions_InvalidUsername_400Exception() {
        // when
        assertThatThrownBy(() -> {
            userService.calculateContributions("invalidName");
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
        LoginUser loginUser = new LoginUser("testUser", "token");
        User user = UserFactory.user("testUser");

        userRepository.save(user);

        // when
        ProfileEditRequestDto requestDto = ProfileEditRequestDto
            .builder()
            .image(FileFactory.getTestImage1())
            .decription(updatedDescription)
            .build();
        ProfileEditResponseDto responseDto =
            userService.editProfile(loginUser, requestDto);

        // then
        assertThat(responseDto.getImageUrl()).isNotBlank();
        assertThat(responseDto.getDescription()).isEqualTo(updatedDescription);
    }

    @DisplayName("자신의 프로필(한 줄 소개만 포함)을 수정할 수 있다.")
    @Test
    void editUserProfile_WithDescription_Success() {
        // given
        String updatedDescription = "updated description";
        LoginUser loginUser = new LoginUser("testUser", "token");
        User user = UserFactory.user("testUser");

        userRepository.save(user);

        // when
        ProfileEditRequestDto requestDto = ProfileEditRequestDto
            .builder()
            .image(FileFactory.getEmptyTestFile())
            .decription(updatedDescription)
            .build();
        ProfileEditResponseDto responseDto =
            userService.editProfile(loginUser, requestDto);

        // then
        assertThat(responseDto.getImageUrl()).isEqualTo(user.getImage());
        assertThat(responseDto.getDescription()).isEqualTo(updatedDescription);
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

        userRepository.save(loginUser);
        searchedUsers.forEach(user -> userRepository.save(user));

        // when
        userService.followUser(new AuthUserRequestDto(loginUser.getName()), searchedUsers.get(0).getName());

        List<UserSearchResponseDto> searchResult =
            userService.searchUser(new LoginUser(loginUser.getName(), "token"),
                userSearchRequestDto);

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
        List<User> userInDb = UserFactory.mockSearchUsers();
        userRepository.saveAll(userInDb);

        // when
        List<UserSearchResponseDto> searchResult =
            userService.searchUser(new GuestUser(), userSearchRequestDto);

        // then
        assertThat(searchResult)
            .extracting("username", "imageUrl", "following")
            .containsExactly(
                tuple(userInDb.get(0).getName(), userInDb.get(0).getImage(), null),
                tuple(userInDb.get(1).getName(), userInDb.get(1).getImage(), null),
                tuple(userInDb.get(2).getName(), userInDb.get(2).getImage(), null)
            );
    }
}
