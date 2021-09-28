package com.woowacourse.pickgit.acceptance.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthLoginUrlResponse;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class OAuthAcceptanceTest extends AcceptanceTest {

    @MockBean
    private OAuthClient oAuthClient;

    @DisplayName("로그인 - Github OAuth 로그인 URL을 요청한다.")
    @Test
    void githubAuthorizationUrl_Github_ReturnLoginUrl() {
        // mock
        when(oAuthClient.getLoginUrl()).thenReturn("https://github.com/login/oauth/authorize?");

        // when
        OAuthLoginUrlResponse response = RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/authorization/github")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract().as(OAuthLoginUrlResponse.class);

        // then
        assertThat(
            response.getUrl().startsWith("https://github.com/login/oauth/authorize")
        ).isTrue();
    }

    @DisplayName("첫 로그인 - Github 인증후 리다이렉션을 통해 요청이 오면 토큰을 생성하여 반환한다.")
    @Test
    void afterAuthorizeGithubLogin_InitialLogin_ReturnJwtToken() {
        //given
        OAuthProfileResponse oAuthProfileResponse = OAuthProfileResponse.builder()
            .name("pick-git-login")
            .description("hi~")
            .githubUrl("github.com/")
            .build();

        // then
        OAuthTokenResponse tokenResponse = 로그인_되어있음(oAuthProfileResponse);
        assertThat(tokenResponse.getToken()).isNotBlank();
        assertThat(tokenResponse.getUsername()).isEqualTo("pick-git-login");
    }

    @DisplayName("재 로그인 - 첫 로그인 이후로 동일한 유저로 로그인하면 정보 업데이트 후 토큰을 생성하여 반환한다.")
    @Test
    void afterAuthorizeGithubLogin_ReLogin_ReturnJwtToken() {
        //given
        OAuthProfileResponse previousOAuthProfileResponse = OAuthProfileResponse.builder()
            .name("pick-git-login")
            .description("hi~")
            .githubUrl("github.com/")
            .build();

        OAuthProfileResponse afterOAuthProfileResponse = OAuthProfileResponse.builder()
            .name("pick-git-login")
            .description("bye~")
            .githubUrl("github.com/")
            .build();

        //when
        로그인_되어있음(previousOAuthProfileResponse);

        // then
        OAuthTokenResponse tokenResponse = 로그인_되어있음(afterOAuthProfileResponse);
        assertThat(tokenResponse.getToken()).isNotBlank();
        assertThat(tokenResponse.getUsername()).isEqualTo("pick-git-login");
    }

    private OAuthTokenResponse 로그인_되어있음(OAuthProfileResponse oAuthProfileResponse) {
        OAuthTokenResponse response = 로그인_요청(oAuthProfileResponse).as(OAuthTokenResponse.class);
        assertThat(response.getToken()).isNotBlank();
        return response;
    }

    private ExtractableResponse<Response> 로그인_요청(OAuthProfileResponse oAuthProfileResponse) {
        // given
        String oauthCode = "1234";
        String accessToken = "oauth.access.token";

        // mock
        when(oAuthClient.getAccessToken(oauthCode)).thenReturn(accessToken);
        when(oAuthClient.getGithubProfile(accessToken)).thenReturn(oAuthProfileResponse);

        // when
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/afterlogin?code=" + oauthCode)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }
}
