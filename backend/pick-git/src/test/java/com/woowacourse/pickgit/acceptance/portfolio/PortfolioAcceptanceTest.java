package com.woowacourse.pickgit.acceptance.portfolio;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.common.factory.PortfolioFactory;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.PortfolioRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.PortfolioResponse;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class PortfolioAcceptanceTest extends AcceptanceTest {

    private static final String USERNAME = "dani";
    private static final String ANOTHER_USERNAME = "neozal";

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        tagRepository.save(new Tag("java"));
        tagRepository.save(new Tag("spring"));
    }

    @DisplayName("사용자는")
    @Nested
    class LoginUser {

        @DisplayName("포트폴리오를 수정한다.")
        @Nested
        class update {

            @DisplayName("나의 포트폴리오, 모든 내용, 연락처/프로젝트/섹션 1개 - 성공")
            @Test
            void update_LoginUserWithMineWithNewAllAndSingleSize_Success() {
                // given
                String token = 로그인_되어있음(USERNAME).getToken();

                authenticatedWithReadApi(token, USERNAME);

                PortfolioRequest request = PortfolioFactory
                    .mockPortfolioRequestWithNewAllAndSingleSize(USERNAME);

                // when
                PortfolioResponse actual = authenticatedWithUpdateApi(token, request, OK)
                    .as(PortfolioResponse.class);

                // then
                PortfolioResponse expected = authenticatedWithReadApi(token, USERNAME)
                    .as(PortfolioResponse.class);

                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
            }

            @DisplayName("나의 포트폴리오, 모든 내용, 연락처/프로젝트/섹션 2개 이상 - 성공")
            @Test
            void update_LoginUserWithMineWithNewAllAndMultipleSize_Success() {
                // given
                String token = 로그인_되어있음(USERNAME).getToken();

                authenticatedWithReadApi(token, USERNAME);

                PortfolioRequest request = PortfolioFactory
                    .mockPortfolioRequestWithNewAllAndMultipleSize(USERNAME);

                // when
                PortfolioResponse actual = authenticatedWithUpdateApi(token, request, OK)
                    .as(PortfolioResponse.class);

                // then
                PortfolioResponse expected = authenticatedWithReadApi(token, USERNAME)
                    .as(PortfolioResponse.class);

                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
            }

            @DisplayName("나의 포트폴리오, 프로필 및 소개 - 성공")
            @Test
            void update_LoginUserWithMineWithNewProfileAndIntroduction_Success() {
                // given
                String token = 로그인_되어있음(USERNAME).getToken();

                authenticatedWithReadApi(token, USERNAME);

                PortfolioRequest request = PortfolioFactory
                    .mockPortfolioRequestWithNewProfileAndIntroduction(USERNAME);

                // when
                PortfolioResponse actual = authenticatedWithUpdateApi(token, request, OK)
                    .as(PortfolioResponse.class);

                // then
                PortfolioResponse expected = authenticatedWithReadApi(token, USERNAME)
                    .as(PortfolioResponse.class);

                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
            }

            @DisplayName("나의 포트폴리오, 연락처 - 성공")
            @Test
            void update_LoginUserWithMineWithNewContact_Success() {
                // given
                String token = 로그인_되어있음(USERNAME).getToken();

                authenticatedWithReadApi(token, USERNAME);

                PortfolioRequest request = PortfolioFactory
                    .mockPortfolioRequestWithNewContact(USERNAME);

                // when
                PortfolioResponse actual = authenticatedWithUpdateApi(token, request, OK)
                    .as(PortfolioResponse.class);

                // then
                PortfolioResponse expected = authenticatedWithReadApi(token, USERNAME)
                    .as(PortfolioResponse.class);

                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
            }

            @DisplayName("나의 포트폴리오, 프로젝트 - 성공")
            @Test
            void update_LoginUserWithMineWithNewProject_Success() {
                // given
                String token = 로그인_되어있음(USERNAME).getToken();

                authenticatedWithReadApi(token, USERNAME);

                PortfolioRequest request = PortfolioFactory
                    .mockPortfolioRequestWithNewProject(USERNAME);

                // when
                PortfolioResponse actual = authenticatedWithUpdateApi(token, request, OK)
                    .as(PortfolioResponse.class);

                // then
                PortfolioResponse expected = authenticatedWithReadApi(token, USERNAME)
                    .as(PortfolioResponse.class);

                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
            }

            @DisplayName("나의 포트폴리오, 섹션 - 성공")
            @Test
            void update_LoginUserWithMineWithNewSection_Success() {
                // given
                String token = 로그인_되어있음(USERNAME).getToken();

                authenticatedWithReadApi(token, USERNAME);

                PortfolioRequest request = PortfolioFactory
                    .mockPortfolioRequestWithNewSection(USERNAME);

                // when
                PortfolioResponse actual = authenticatedWithUpdateApi(token, request, OK)
                    .as(PortfolioResponse.class);

                // then
                PortfolioResponse expected = authenticatedWithReadApi(token, USERNAME)
                    .as(PortfolioResponse.class);

                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
            }

            @DisplayName("남의 포트폴리오 - 실패")
            @Test
            void update_LoginUserWithYours_Fail() {
                // given
                String token = 로그인_되어있음(USERNAME).getToken();
                String anotherToken = 로그인_되어있음(ANOTHER_USERNAME).getToken();

                authenticatedWithReadApi(anotherToken, ANOTHER_USERNAME);

                PortfolioRequest request = PortfolioFactory
                    .mockPortfolioRequestWithNewAllAndSingleSize(ANOTHER_USERNAME);

                // when
                ApiErrorResponse response = authenticatedWithUpdateApi(token, request, UNAUTHORIZED)
                    .as(ApiErrorResponse.class);

                // then
                assertThat(response.getErrorCode()).isEqualTo("A0002");
            }
        }
    }

    @DisplayName("게스트는")
    @Nested
    class GuestUser {

        @DisplayName("포트폴리오를 수정한다.")
        @Nested
        class update {

            @DisplayName("남의 포트폴리오 - 실패")
            @Test
            void update_GuestUserWithYours_Fail() {
                // given
                PortfolioRequest request = PortfolioFactory
                    .mockPortfolioRequestWithNewAllAndSingleSize(ANOTHER_USERNAME);

                // when
                ApiErrorResponse response = unauthenticatedWithUpdateApi(request)
                    .as(ApiErrorResponse.class);

                // then
                assertThat(response.getErrorCode()).isEqualTo("A0001");
            }
        }
    }

    private ExtractableResponse<Response> authenticatedWithReadApi(String token, String username) {
        return given().log().all()
            .auth().oauth2(token)
            .when()
            .get("/api/portfolios/{username}", username)
            .then().log().all()
            .statusCode(OK.value())
            .extract();
    }

    private ExtractableResponse<Response> authenticatedWithUpdateApi(
        String token,
        PortfolioRequest request,
        HttpStatus httpStatus
    ) {
        return given().log().all()
            .auth().oauth2(token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .put("/api/portfolios")
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

    private ExtractableResponse<Response> unauthenticatedWithUpdateApi(PortfolioRequest request) {
        return given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .put("/api/portfolios")
            .then().log().all()
            .statusCode(UNAUTHORIZED.value())
            .extract();
    }
}
