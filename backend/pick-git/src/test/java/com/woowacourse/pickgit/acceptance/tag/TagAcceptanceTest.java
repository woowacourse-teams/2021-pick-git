package com.woowacourse.pickgit.acceptance.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
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

@Import(InfrastructureTestConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class TagAcceptanceTest {

    @LocalServerPort
    private int port;

    @MockBean
    private OAuthClient oAuthClient;

    private String accessToken;
    private final String repositoryName = "doms-react";

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        OAuthTokenResponse tokenResponse = 로그인_되어있음();
        accessToken = tokenResponse.getToken();
    }

    private ExtractableResponse<Response> requestTags(String accessToken, String url,
        HttpStatus httpStatus) {
        return RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .when().get(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    @DisplayName("특정 User의 Repository에 기술된 언어 태그들을 추출한다.")
    @Test
    void extractLanguageTags_ValidRepository_ExtractionSuccess() {
        // given
        String url =
            "/api/github/repositories/" + repositoryName + "/tags/languages";

        // when
        List<String> response = requestTags(accessToken, url, HttpStatus.OK)
            .as(new TypeRef<List<String>>() {
            });

        // then
        assertThat(response).containsExactly("javascript", "html", "css");
    }

    @DisplayName("유효하지 않은 레포지토리 태그 추출 요청시 500 예외 메시지가 반환된다.")
    @Test
    void extractLanguageTags_InvalidRepository_ExceptionThrown() {
        // given
        String url =
            "/api/github/repositories/none-available-repo/tags/languages";

        // when
        ApiErrorResponse response = requestTags(accessToken, url, HttpStatus.INTERNAL_SERVER_ERROR)
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("V0001");
    }

    @DisplayName("유효하지 않은 AccessToken으로 태그 추출 요청시 401 예외가 발생한다.")
    @Test
    void extractLanguageTags_InvalidAccessToken_ExceptionThrown() {
        // given
        String url =
            "/api/github/repositories/" + repositoryName + "/tags/languages";

        // when
        ApiErrorResponse response =
            requestTags("invalidtoken", url, HttpStatus.UNAUTHORIZED)
                .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("토큰을 포함하지 않고 태그 추출 요청시 401 예외가 발생한다.")
    @Test
    void extractLanguageTags_EmptyToken_ExceptionThrown() {
        // given
        String url =
            "/api/github/repositories/" + repositoryName + "/tags/languages";

        // when
        ApiErrorResponse response = RestAssured.given().log().all()
            .when().get(url)
            .then().log().all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .extract()
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    private OAuthTokenResponse 로그인_되어있음() {
        OAuthTokenResponse response = 로그인_요청().as(OAuthTokenResponse.class);
        assertThat(response.getToken()).isNotBlank();
        return response;
    }

    private ExtractableResponse<Response> 로그인_요청() {
        // given
        String oauthCode = "1234";
        String accessToken = "oauth.access.token";

        OAuthProfileResponse oAuthProfileResponse = OAuthProfileResponse
            .builder()
            .name("jipark3")
            .image("image")
            .description("hi~")
            .githubUrl("htts://www.github.com/")
            .build();

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
