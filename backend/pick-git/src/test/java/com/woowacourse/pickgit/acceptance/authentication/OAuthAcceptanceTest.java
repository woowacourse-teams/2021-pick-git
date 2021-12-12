package com.woowacourse.pickgit.acceptance.authentication;

import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthLoginUrlResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuthAcceptanceTest extends AcceptanceTest {

    @DisplayName("로그인 - Github OAuth 로그인 URL을 요청한다.")
    @Test
    void githubAuthorizationUrl_Github_ReturnLoginUrl() {
        OAuthLoginUrlResponse response = NEOZAL.는().OAuth_로그인_URL_요청을_한다()
            .as(OAuthLoginUrlResponse.class);

        assertThat(response.getUrl()).startsWith("https://github.com/login/oauth/authorize");
    }

    @DisplayName("첫 로그인 - Github 인증후 리다이렉션을 통해 요청이 오면 토큰을 생성하여 반환한다.")
    @Test
    void afterAuthorizeGithubLogin_InitialLogin_ReturnJwtToken() {
        TokenDto tokenResponse = NEOZAL.은로그인을한다();
        assertThat(tokenResponse.getToken()).isNotBlank();
        assertThat(tokenResponse.getUsername()).isEqualTo("NEOZAL");
    }

    @DisplayName("재 로그인 - 첫 로그인 이후로 동일한 유저로 로그인하면 정보 업데이트 후 토큰을 생성하여 반환한다.")
    @Test
    void afterAuthorizeGithubLogin_ReLogin_ReturnJwtToken() {
        NEOZAL.은로그인을한다();
        TokenDto tokenResponse = NEOZAL.은로그인을한다();

        assertThat(tokenResponse.getToken()).isNotBlank();
        assertThat(tokenResponse.getUsername()).isEqualTo("NEOZAL");
    }
}
