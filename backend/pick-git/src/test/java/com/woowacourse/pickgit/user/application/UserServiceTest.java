package com.woowacourse.pickgit.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.woowacourse.pickgit.user.application.dto.UserProfileServiceDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String NAME = "yjksw";
    private static final String IMAGE = "http://img.com";
    private static final String DESCRIPTION = "The Best";
    private static final String GITHUB_URL = "www.github.com/yjksw";
    private static final String COMPANY = "woowacourse";
    private static final String LOCATION = "Seoul";
    private static final String WEBSITE = "www.pick-git.com";
    private static final String TWITTER = "pick-git twitter";

    private BasicProfile basicProfile;
    private GithubProfile githubProfile;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        this.basicProfile = new BasicProfile(NAME, IMAGE, DESCRIPTION);
        this.githubProfile = new GithubProfile(GITHUB_URL, COMPANY, LOCATION, WEBSITE, TWITTER);
    }

    @DisplayName("유저이름으로 검색한 User 기반으로 프로필 정보를 성공적으로 가져온다.")
    @Test
    public void getUserProfile_FindUserInfoByName_Success() {
        //given
        User user = new User(basicProfile, githubProfile);

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));

        UserProfileServiceDto expectedUserProfileDto = new UserProfileServiceDto(
            NAME, IMAGE, DESCRIPTION,
            0, 0, 0,
            COMPANY, LOCATION, WEBSITE, TWITTER
        );

        //when
        UserProfileServiceDto actualUserProfileDto = userService.getUserProfile(NAME);

        //then
        assertThat(actualUserProfileDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedUserProfileDto);
    }
}
