package com.woowacourse.pickgit.integration.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.authentication.application.JwtTokenProvider;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.dao.OAuthAccessTokenDao;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.config.TestApiConfiguration;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestApiConfiguration.class)
@DisplayName("OAuthService 통합 테스트 (UserRepository 사용)")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class OAuthServiceIntegrationTest {

    @MockBean
    private OAuthClient oAuthClient;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private OAuthAccessTokenDao oAuthAccessTokenDao;

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

    @DisplayName("JWT 토큰을 통해 AccessTokenDB에서 LoginUser에 대한 정보를 가져온다.")
    @Test
    void findRequestUserByToken_ValidToken_ReturnAppUser() {
        // given
        String username = "pick-git";
        String token = jwtTokenProvider.createToken(username);
        String accessToken = "oauth access token";

        oAuthAccessTokenDao.insert(token, accessToken);

        // when
        AppUser appUser = oAuthService.findRequestUserByToken(token);

        // then
        assertThat(appUser).isInstanceOf(LoginUser.class);
        assertThat(appUser.getUsername()).isEqualTo(username);
        assertThat(appUser.getAccessToken()).isEqualTo(accessToken);
    }

    @DisplayName("AccessTokenDB에 저장되어 있지 않은 JWT 토큰이라면 예외가 발생한다.")
    @Test
    void findRequestUserByToken_NotFoundToken_ThrowException() {
        // given
        String username = "pick-git-test";
        String token = jwtTokenProvider.createToken(username);

        // when, then
        assertThatThrownBy(() -> oAuthService.findRequestUserByToken(token))
            .isInstanceOf(InvalidTokenException.class);
    }
}
