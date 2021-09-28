package com.woowacourse.pickgit.acceptance.portfolio;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.common.factory.PortfolioFactory;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.PortfolioResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class PortfolioAcceptanceTest extends AcceptanceTest {

    private static final String USERNAME = "dani";
    private static final String ANOTHER_USERNAME = "neozal";

    @MockBean
    private OAuthClient oAuthClient;

    @DisplayName("사용자는 포트폴리오를 조회한다. - 내 것")
    @Test
    void read_LoginUserWithMine_Success() {
        // given
        String token = 로그인_되어있음(USERNAME).getToken();

        PortfolioResponse expected = PortfolioFactory.mockPortfolioResponse();

        // when
        PortfolioResponse response = authenticatedWithReadApi(token, USERNAME, OK)
            .as(PortfolioResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @DisplayName("사용자는 포트폴리오를 조회한다. - 남 것")
    @Test
    void read_LoginUserWithYours_Success() {
        // given
        String token = 로그인_되어있음(USERNAME).getToken();
        로그인_되어있음(ANOTHER_USERNAME);

        PortfolioResponse expected = PortfolioFactory.mockPortfolioResponse();

        // when
        PortfolioResponse response = authenticatedWithReadApi(token, ANOTHER_USERNAME, OK)
            .as(PortfolioResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @DisplayName("게스트는 포트폴리오를 조회한다. - 포트폴리오 존재 O")
    @Test
    void read_GuestUser_Success() {
        // given
        String token = 로그인_되어있음(USERNAME).getToken();
        authenticatedWithReadApi(token, USERNAME, OK);

        PortfolioResponse expected = PortfolioFactory.mockPortfolioResponse();

        // when
        PortfolioResponse response = unauthenticatedWithReadApi(OK)
            .as(PortfolioResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @DisplayName("게스트는 포트폴리오를 조회한다. - 포트폴리오 존재 X")
    @Test
    void read_GuestUser_Fail() {
        // when
        ApiErrorResponse response = unauthenticatedWithReadApi(BAD_REQUEST)
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("R0001");
    }

    private ExtractableResponse<Response> authenticatedWithReadApi(
        String token,
        String username,
        HttpStatus httpStatus
    ) {
        return given().log().all()
            .auth().oauth2(token)
            .when()
            .get("/api/portfolios/{username}", username)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    private ExtractableResponse<Response> unauthenticatedWithReadApi(HttpStatus httpStatus) {
        return given().log().all()
            .when()
            .get("/api/portfolios/{username}", USERNAME)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    @DisplayName("사용자는 포트폴리오를 수정한다.")
    @Test
    void update_LoginUser_Success() {
        // given

        // when

        // then
    }

    @DisplayName("게스트는 포트폴리오를 수정할 수 없다.")
    @Test
    void update_GuestUser_Fail() {
        // given

        // when

        // then
    }

    private OAuthTokenResponse 로그인_되어있음(String name) {
        OAuthTokenResponse response = 로그인_요청(name)
            .as(OAuthTokenResponse.class);

        assertThat(response.getToken()).isNotBlank();

        return response;
    }

    private ExtractableResponse<Response> 로그인_요청(String name) {
        // given
        String oauthCode = "1234";
        String accessToken = "oauth.access.token";

        OAuthProfileResponse oAuthProfileResponse = new OAuthProfileResponse(
            name, "image", "hi~", "github.com/",
            null, null, null, null
        );

        given(oAuthClient.getAccessToken(oauthCode))
            .willReturn(accessToken);
        given(oAuthClient.getGithubProfile(accessToken))
            .willReturn(oAuthProfileResponse);

        // when
        return RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/afterlogin?code=" + oauthCode)
            .then().log().all()
            .statusCode(OK.value())
            .extract();
    }
}
