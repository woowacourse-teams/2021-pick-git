package com.woowacourse.pickgit.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthLoginUrlResponse;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

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
        // given
        String oauthCode = "1234";
        String accessToken = "oauth.access.token";

        OAuthProfileResponse oAuthProfileResponse = new OAuthProfileResponse(
            "pick-git", "image", "hi~", "github.com/",
            null, null, null, null
        );

        // mock
        when(oAuthClient.getAccessToken(oauthCode)).thenReturn(accessToken);
        when(oAuthClient.getGithubProfile(accessToken)).thenReturn(oAuthProfileResponse);

        // when
        OAuthTokenResponse response = RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/afterlogin?code=" + oauthCode)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract().as(OAuthTokenResponse.class);

        // then
        assertThat(response.getToken()).isNotBlank();
    }
}
