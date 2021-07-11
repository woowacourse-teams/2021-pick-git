package com.woowacourse.pickgit.authentication.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.dao.CollectionOAuthAccessTokenDao;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

@DisplayName("OAuthService 단위 테스트 - ")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class OAuthServiceTest {

    @Autowired
    private OAuthService oAuthService;

    @MockBean
    private OAuthClient oAuthClient;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CollectionOAuthAccessTokenDao oAuthAccessTokenDao;

    @DisplayName("Github 로그인 URL을 반환한다.")
    @Test
    void getGithubAuthorizationUrl_Anonymous_ReturnGithubAuthorizationUrl() {
        // given
        String url = "https://github.com/login..";

        // mock
        when(oAuthClient.getLoginUrl()).thenReturn(url);

        // then
        assertThat(oAuthService.getGithubAuthorizationUrl()).isEqualTo(url);
    }

    @DisplayName("회원가입(첫 로그인)시 Github Profile을 가져와서 DB에 저장한다.")
    @Test
    void createToken_Signup_SaveUserProfile() {
        // given
        String code = "oauth authorization code";
        String oauthAccessToken = "oauth access token";

        OAuthProfileResponse githubProfileResponse = new OAuthProfileResponse();
        githubProfileResponse.setName("test");
        githubProfileResponse.setDescription("hi~");

        User user = new User(
            githubProfileResponse.toBasicProfile(),
            githubProfileResponse.toGithubProfile()
        );

        // mock
        when(oAuthClient.getAccessToken(code)).thenReturn(oauthAccessToken);
        when(oAuthClient.getGithubProfile(oauthAccessToken)).thenReturn(githubProfileResponse);

    }
}
