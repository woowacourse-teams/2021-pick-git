package com.woowacourse.pickgit.authentication.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("OAuthService 통합 테스트 (UserService 사용)")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
public class OAuthServiceTest {

    @MockBean
    private OAuthClient oAuthClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuthService oAuthService;

    @DisplayName("Github 로그인 URL을 반환한다.")
    @Test
    void getGithubAuthorizationUrl_Anonymous_ReturnGithubAuthorizationUrl() {
        // mock
        when(oAuthClient.getLoginUrl()).thenReturn("https://github.com/login/oauth/authorize?");

        // when
        String githubAuthorizationUrl = oAuthService.getGithubAuthorizationUrl();

        // then
        assertThat(githubAuthorizationUrl).startsWith("https://github.com/login/oauth/authorize?");
    }

    @DisplayName("회원가입(첫 로그인)시 Github Profile을 가져와서 DB에 insert한다.")
    @Test
    void createToken_Signup_SaveUserProfile() {
        // given
        String code = "oauth authorization code";
        String oauthAccessToken = "oauth access token";
        OAuthProfileResponse oAuthProfileResponse = new OAuthProfileResponse(
            "binghe", "image", null, "github.com/",
            null, null, null, null
        );

        // mock
        when(oAuthClient.getAccessToken(code)).thenReturn(oauthAccessToken);
        when(oAuthClient.getGithubProfile(oauthAccessToken))
            .thenReturn(oAuthProfileResponse);

        // when
        oAuthService.createToken(code);

        // then
        User user = userRepository.findByBasicProfile_Name(oAuthProfileResponse.getName()).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getBasicProfile().getName()).isEqualTo("binghe");
        assertThat(user.getGithubProfile().getGithubUrl()).isEqualTo("github.com/");
    }

    @DisplayName("로그인(첫 로그인이 아닌경우)시 Github Profile을 가져와서 DB에 저장된 기존 정보를 update한다.")
    @Test
    void createToken_Signup_UpdateUserProfile() {
        // given
        String code = "oauth authorization code";
        String oauthAccessToken = "oauth access token";
        OAuthProfileResponse oAuthProfileResponse = new OAuthProfileResponse(
            "binghe", "image", null, "github.com/",
            null, null, null, null
        );

        // mock
        when(oAuthClient.getAccessToken(code)).thenReturn(oauthAccessToken);
        when(oAuthClient.getGithubProfile(oauthAccessToken))
            .thenReturn(oAuthProfileResponse);

        // when
        oAuthService.createToken(code);

        oAuthProfileResponse.setCompany("@woowabros");
        oAuthService.createToken(code);

        // then
        User user = userRepository.findByBasicProfile_Name(oAuthProfileResponse.getName()).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getGithubProfile().getCompany()).isEqualTo("@woowabros");
    }
}
