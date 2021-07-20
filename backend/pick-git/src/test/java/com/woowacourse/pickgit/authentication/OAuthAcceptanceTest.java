package com.woowacourse.pickgit.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthLoginUrlResponse;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.post.PostTestConfiguration;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@Import(PostTestConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class OAuthAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @MockBean
    private OAuthClient oAuthClient;

    @DisplayName("로그인 - Github OAuth 로그인 URL을 요청한다.")
    @Test
    void Authorization_Github_ReturnLoginUrl() {
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

    @DisplayName("로그인 - Github 인증후 리다이렉션을 통해 요청이 오면 토큰을 생성하여 반환한다.")
    @Test
    void Authorization_Redirection_ReturnJwtToken() {
        // then
        OAuthTokenResponse tokenResponse = 로그인_되어있음();
        assertThat(tokenResponse.getToken()).isNotBlank();
        assertThat(tokenResponse.getUsername()).isEqualTo("pick-git-login");
    }

    public OAuthTokenResponse 로그인_되어있음() {
        OAuthTokenResponse response = 로그인_요청().as(OAuthTokenResponse.class);
        assertThat(response.getToken()).isNotBlank();
        return response;
    }

    public ExtractableResponse<Response> 로그인_요청() {
        // given
        String oauthCode = "1234";
        String accessToken = "oauth.access.token";

        OAuthProfileResponse oAuthProfileResponse = new OAuthProfileResponse(
            "pick-git-login", "image", "hi~", "github.com/",
            null, null, null, null
        );

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
