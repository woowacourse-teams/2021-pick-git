package com.woowacourse.pickgit.query.acceptance.portfolio;

import static com.woowacourse.pickgit.query.fixture.TUser.KODA;
import static com.woowacourse.pickgit.query.fixture.TUser.MARK;
import static com.woowacourse.pickgit.query.fixture.TUser.NEOZAL;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.common.factory.PortfolioFactory;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.PortfolioResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PortfolioAcceptanceTest_Query extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        toRead();
    }

    @DisplayName("사용자는 포트폴리오를 '최초' 조회한다 나의 포트폴리오 - 성공")
    @Test
    void read_LoginUserWithMine_Success() {
        // given
        String token = NEOZAL.은로그인을한다();

        PortfolioResponse expected = PortfolioFactory
            .mockPortfolioResponse(NEOZAL.name());

        // when
        PortfolioResponse response = authenticatedWithReadApiWithStatus(token, NEOZAL.name())
            .as(PortfolioResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expected);
    }

    @DisplayName("사용자는 포트폴리오를 조회한다 남의 포트폴리오, 포트폴리오가 존재하는 경우 - 성공")
    @Test
    void read_LoginUserWithYours_IfExisting_Success() {
        // given
        String token = NEOZAL.은로그인을한다();
        String kodaAccessToken = KODA.은로그인을한다();

        authenticatedWithReadApiWithStatus(kodaAccessToken, KODA.name())
            .as(PortfolioResponse.class);

        PortfolioResponse expected = PortfolioFactory
            .mockPortfolioResponse(KODA.name());

        // when
        PortfolioResponse response = authenticatedWithReadApiWithStatus(token, KODA.name())
            .as(PortfolioResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt", "id")
            .isEqualTo(expected);
    }

    @DisplayName("사용자는 포트폴리오를 조회한다 남의 포트폴리오, 포트폴리오가 없는 경우 - 실패")
    @Test
    void read_LoginUserWithYours_IfNotExisting_Fail() {
        // given
        String token = NEOZAL.은로그인을한다();
        KODA.은로그인을한다();

        // when
        ApiErrorResponse response = authenticatedWithReadApiWithoutStatus(token, KODA.name())
            .statusCode(BAD_REQUEST.value())
            .extract()
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("R0001");
    }

    @DisplayName("게스트는 포트폴리오를 조회한다 남의 포트폴리오, 포트폴리오가 존재하는 경우 - 성공")
    @Test
    void read_GuestUserWithExistingYours_Success() {
        // given
        String token = NEOZAL.은로그인을한다();
        authenticatedWithReadApiWithStatus(token, NEOZAL.name());

        PortfolioResponse expected = PortfolioFactory
            .mockPortfolioResponse(NEOZAL.name());

        // when
        PortfolioResponse response = unauthenticatedWithReadApi(NEOZAL.name(), OK)
            .as(PortfolioResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expected);
    }

    @DisplayName("게스트는 포트폴리오를 조회한다 남의 포트폴리오, 포트폴리오가 존재하지 않는 경우 - 실패")
    @Test
    void read_GuestUserWithNonExistingYours_Fail() {
        // when
        ApiErrorResponse response = unauthenticatedWithReadApi(MARK.name(), BAD_REQUEST)
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("R0001");
    }
    
    private ExtractableResponse<Response> authenticatedWithReadApiWithStatus(String token, String username) {
        return authenticatedWithReadApiWithoutStatus(token, username)
            .statusCode(OK.value())
            .extract();
    }

    private ValidatableResponse authenticatedWithReadApiWithoutStatus(String token, String username) {
        return given().log().all()
            .auth().oauth2(token)
            .when()
            .get("/api/portfolios/{username}", username)
            .then().log().all();
    }

    private ExtractableResponse<Response> unauthenticatedWithReadApi(String name,
        HttpStatus httpStatus) {
        return given().log().all()
            .when()
            .get("/api/portfolios/{username}", name)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }
}
