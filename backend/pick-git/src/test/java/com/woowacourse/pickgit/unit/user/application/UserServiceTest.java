package com.woowacourse.pickgit.unit.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.woowacourse.pickgit.post.domain.PickGitStorage;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.domain.Contribution;
import com.woowacourse.pickgit.user.domain.PlatformContributionCalculator;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.application.dto.request.UserSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import java.io.File;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlatformContributionCalculator platformContributionCalculator;

    @Mock
    private PickGitStorage pickGitStorage;

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
        User user = UserFactory.user();

        Contribution contribution = new Contribution(11, 48, 48, 48, 48);

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
        given(platformContributionCalculator.calculate(anyString()))
            .willReturn(contribution);

        ContributionResponseDto responseDto = UserFactory.mockContributionResponseDto();

        // when
        ContributionResponseDto contributions = userService.calculateContributions("testUser");

        // then
        assertThat(contributions)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);

        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
        verify(platformContributionCalculator, times(1))
            .calculate(anyString());
    }

    @DisplayName("존재하지 않는 유저 이름으로 활동 통계를 조회할 수 없다. - 400 예외")
    @Test
    void calculateContributions_InvalidUsername_400Exception() {
        // when
        assertThatThrownBy(() -> {
            userService.calculateContributions("invalidName");
        }).isInstanceOf(InvalidUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("유효하지 않은 유저입니다.");

        // then
        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
    }

    @DisplayName("자신의 프로필(이미지, 한 줄 소개 포함)을 수정할 수 있다.")
    @Test
    void editUserProfile_WithImageAndDescription_Success() {
        // given
        LoginUser loginUser = new LoginUser("testUser", "token");
        MultipartFile image = FileFactory.getTestImage1();
        String updatedDescription = "updated description";

        // mock
        given(userRepository.findByBasicProfile_Name("testUser"))
            .willReturn(Optional.of(UserFactory.user(1L, "testUser")));
        given(pickGitStorage.store(any(File.class), anyString()))
            .willReturn(Optional.ofNullable(image.getName()));

        // when
        ProfileEditRequestDto requestDto = ProfileEditRequestDto
            .builder()
            .image(image)
            .decription(updatedDescription)
            .build();
        ProfileEditResponseDto responseDto = userService.editProfile(loginUser, requestDto);

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
    void editUserProfile_WithDescrption_Success() {
        // given
        LoginUser loginUser = new LoginUser("testUser", "token");
        User user = UserFactory.user(1L, "testUser");
        String updatedDescription = "updated descrption";

        // mock
        given(userRepository.findByBasicProfile_Name("testUser"))
            .willReturn(Optional.of(user));

        // when
        ProfileEditRequestDto requestDto = ProfileEditRequestDto
            .builder()
            .image(FileFactory.getEmptyTestFile())
            .decription(updatedDescription)
            .build();
        ProfileEditResponseDto responseDto = userService.editProfile(loginUser, requestDto);

        // then
        assertThat(responseDto.getImageUrl()).isEqualTo(user.getImage());
        assertThat(responseDto.getDescription()).isEqualTo(updatedDescription);
        verify(userRepository, times(1))
            .findByBasicProfile_Name(user.getName());
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

        // mock
        given(userRepository.searchByUsernameLike(searchKeyword, PageRequest.of(page, limit)))
            .willReturn(searchedUser);
        given(userRepository.findByBasicProfile_Name(loginUser.getName()))
            .willReturn(Optional.ofNullable(loginUser));

        // when
        loginUser.follow(searchedUser.get(0));
        List<UserSearchResponseDto> searchResponses = userService
            .searchUser(new LoginUser(loginUser.getName(), "token"), userSearchRequestDto);

        // then
        assertThat(searchResponses).hasSize(4);
        assertThat(searchResponses)
            .extracting("username")
            .containsExactly(searchedUser.stream().map(User::getName).toArray());
        assertThat(searchResponses)
            .extracting("following")
            .containsExactly(true, false, false, false);
        verify(userRepository, times(1)).searchByUsernameLike(searchKeyword, PageRequest.of(page, limit));
        verify(userRepository, times(1)).findByBasicProfile_Name(loginUser.getName());
    }

    @DisplayName("비 로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색한다. (팔로잉 필드 null)")
    @Test
    void searchUser_GuestUser_Success() {
        // given
        String searchKeyword = "bing";
        int page = 0; int limit = 5;
        List<User> usersInDb = UserFactory.mockSearchUsersWithId();
        UserSearchRequestDto userSearchRequestDto = UserSearchRequestDto
            .builder()
            .keyword(searchKeyword)
            .page(0L)
            .limit(5L)
            .build();

        // mock
        given(userRepository.searchByUsernameLike(searchKeyword, PageRequest.of(page, limit)))
            .willReturn(usersInDb);

        // when
        List<UserSearchResponseDto> searchResult =
            userService.searchUser(new GuestUser(), userSearchRequestDto);

        // then
        assertThat(searchResult).hasSize(5);
        assertThat(searchResult)
            .extracting("username")
            .containsExactly(usersInDb.stream().map(User::getName).toArray());
        assertThat(searchResult)
            .extracting("following")
            .containsExactly(null, null, null, null, null);
        verify(userRepository, times(1)).searchByUsernameLike(searchKeyword, PageRequest.of(page, limit));
        verify(userRepository, times(0)).findByBasicProfile_Name(anyString());
    }
}
