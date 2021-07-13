package com.woowacourse.pickgit.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.user.UserFactory;
import com.woowacourse.pickgit.user.application.dto.AuthUserServiceDto;
import com.woowacourse.pickgit.user.application.dto.FollowServiceDto;
import com.woowacourse.pickgit.user.application.dto.UserProfileServiceDto;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.exception.DuplicatedFollowException;
import com.woowacourse.pickgit.user.exception.InvalidFollowException;
import com.woowacourse.pickgit.user.exception.InvalidUserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class UserServiceIntegrationTest {

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

    private UserFactory userFactory = new UserFactory();
    ;

    @DisplayName("유저이름으로 검색한 User 기반으로 프로필 정보를 성공적으로 가져온다.")
    @Test
    public void getUserProfile_FindUserInfoByName_Success() {
        //given
        userRepository.save(userFactory.user());
        UserProfileServiceDto expectedUserProfileDto = new UserProfileServiceDto(
            NAME, IMAGE, DESCRIPTION,
            0, 0, 0,
            GITHUB_URL, COMPANY, LOCATION, WEBSITE, TWITTER
        );

        //when
        UserProfileServiceDto actualUserProfileDto = userService.getUserProfile(NAME);

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
        assertThatThrownBy(
            () -> userService.getUserProfile("InvalidName")
        ).hasMessage(new InvalidUserException().getMessage());
    }

    @DisplayName("Source 유저가 Target 유저를 follow 하면 성공한다.")
    @Test
    void followUser_ValidUser_Success() {
        //given
        userRepository.save(userFactory.user());
        userRepository.save(userFactory.anotherUser());
        AuthUserServiceDto authUserServiceDto = new AuthUserServiceDto(NAME);
        String targetName = "pickgit";

        //when
        FollowServiceDto followServiceDto = userService.followUser(authUserServiceDto, targetName);

        //then
        assertThat(followServiceDto.getFollowerCount()).isEqualTo(1);
        assertThat(followServiceDto.isFollowing()).isTrue();
    }

    @DisplayName("이미 존재하는 Follow 추가 시 예외가 발생한다.")
    @Test
    void followUser_ExistingFollow_ExceptionThrown() {
        //given
        userRepository.save(userFactory.user());
        userRepository.save(userFactory.anotherUser());
        AuthUserServiceDto authUserServiceDto = new AuthUserServiceDto(NAME);
        String targetName = "pickgit";

        userService.followUser(authUserServiceDto, targetName);

        //when
        //then
        assertThatThrownBy(
            () -> userService.followUser(authUserServiceDto, targetName)
        ).hasMessage(new DuplicatedFollowException().getMessage());
    }

    @DisplayName("Source 유저가 Target 유저를 unfollow 하면 성공한다.")
    @Test
    void unfollowUser_ValidUser_Success() {
        //given
        userRepository.save(userFactory.user());
        userRepository.save(userFactory.anotherUser());
        AuthUserServiceDto authUserServiceDto = new AuthUserServiceDto(NAME);
        String targetName = "pickgit";

        userService.followUser(authUserServiceDto, targetName);

        //when
        FollowServiceDto followServiceDto = userService
            .unfollowUser(authUserServiceDto, targetName);

        //then
        assertThat(followServiceDto.getFollowerCount()).isEqualTo(0);
        assertThat(followServiceDto.isFollowing()).isFalse();
    }

    @DisplayName("존재하지 않는 Follow 관계를 unfollow 하면 예외가 발생한다.")
    @Test
    void unfollowUser_NotExistingFollow_ExceptionThrown() {
        //given
        userRepository.save(userFactory.user());
        userRepository.save(userFactory.anotherUser());
        AuthUserServiceDto authUserServiceDto = new AuthUserServiceDto(NAME);
        String targetName = "pickgit";

        //when
        //then
        assertThatThrownBy(
            () -> userService.unfollowUser(authUserServiceDto, targetName)
        ).hasMessage(new InvalidFollowException().getMessage());
    }
}
