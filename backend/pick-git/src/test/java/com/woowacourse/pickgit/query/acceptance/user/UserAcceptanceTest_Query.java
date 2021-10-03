package com.woowacourse.pickgit.query.acceptance.user;

import static com.woowacourse.pickgit.query.fixture.TUser.DANI;
import static com.woowacourse.pickgit.query.fixture.TUser.KEVIN;
import static com.woowacourse.pickgit.query.fixture.TUser.KODA;
import static com.woowacourse.pickgit.query.fixture.TUser.MARK;
import static com.woowacourse.pickgit.query.fixture.TUser.NEOZAL;
import static com.woowacourse.pickgit.query.fixture.TUser.모든유저;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.presentation.dto.response.UserProfileResponse;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class UserAcceptanceTest_Query extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        모든유저().로그인을한다();

        NEOZAL.은로그인을하고().팔로우를한다(KODA, DANI);
        KODA.은로그인을하고().팔로우를한다(NEOZAL, MARK, DANI);
        MARK.은로그인을하고().팔로우를한다(KODA, DANI);
        DANI.은로그인을하고().팔로우를한다(KEVIN);
    }

    @DisplayName("로그인된 사용자는 자신의 프로필을 조회할 수 있다.")
    @Test
    void getAuthenticatedUserProfile_LoginUser_Success() {
        // given
        String token = NEOZAL.은로그인을한다();
        // when
        UserProfileResponse response =
            authenticatedRequest(
                token,
                "/api/profiles/me",
                Method.GET,
                HttpStatus.OK
            ).as(UserProfileResponse.class);

        // then
        assertThat(response.getName()).isEqualTo(NEOZAL.name());
    }

    @DisplayName("유효하지 않은 토큰을 지닌 사용자는 자신의 프로필을 조회할 수 없다. - 401 예외")
    @Test
    void getAuthenticatedUserProfile_LoginUserWithInvalidToken_401Exception() {
        // when
        ApiErrorResponse response =
            authenticatedRequest(
                "testToken",
                "/api/profiles/me",
                Method.GET,
                HttpStatus.UNAUTHORIZED
            ).as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("토큰이 없는 사용자는 자신의 프로필을 조회할 수 없다. - 401 예외")
    @Test
    void getAuthenticatedUserProfile_LoginUserWithoutToken_401Exception() {
        // when
        ApiErrorResponse response =
            unauthenticatedRequest(
                "/api/profiles/me",
                Method.GET,
                HttpStatus.UNAUTHORIZED
            ).as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("로그인된 사용자는 팔로우한 유저의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_LoginUserIsFollowing_Success() {
        // when
        UserProfileResponse response =
            authenticatedRequest(
                NEOZAL.은로그인을한다(),
                String.format("/api/profiles/%s", KODA),
                Method.GET,
                HttpStatus.OK
            ).as(UserProfileResponse.class);

        // then
        assertThat(response.getName()).isEqualTo(KODA.name());
    }

    @DisplayName("로그인된 사용자는 팔로우하지 않은 유저의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_LoginUserIsNotFollowing_Success() {
        // when
        UserProfileResponse response =
            authenticatedRequest(
                NEOZAL.은로그인을한다(),
                String.format("/api/profiles/%s", MARK.name()),
                Method.GET,
                HttpStatus.OK
            ).as(UserProfileResponse.class);

        // then
        assertThat(response.getName()).isEqualTo(MARK.name());
    }

    @DisplayName("로그인된 사용자는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_LoginUser_400Exception() {
        String token = NEOZAL.은로그인을한다();

        // when
        ApiErrorResponse response =
            authenticatedRequest(
                token,
                String.format("/api/profiles/%s", "invalidName"),
                Method.GET,
                HttpStatus.BAD_REQUEST
            ).as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("U0001");
    }

    @DisplayName("게스트 유저는 다른 유저의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_GuestUser_Success() {
        //given
        MARK.은로그인을한다();

        //when
        UserProfileResponse response = unauthenticatedRequest(
            String.format("/api/profiles/%s", MARK.name()),
            Method.GET,
            HttpStatus.OK
        ).as(UserProfileResponse.class);

        //then
        assertThat(response.getName()).isEqualTo(MARK.name());
    }

    @DisplayName("게스트 유저는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_GuestUser_400Exception() {
        // when
        ApiErrorResponse response = unauthenticatedRequest(
            String.format("/api/profiles/%s", "invalidName"),
            Method.GET,
            HttpStatus.BAD_REQUEST
        ).as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("U0001");
    }

    private ExtractableResponse<Response> authenticatedRequest(
        String accessToken,
        String url,
        Method method,
        HttpStatus httpStatus
    ) {
        return RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .when().request(method, url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    private ExtractableResponse<Response> unauthenticatedRequest(
        String url,
        Method method,
        HttpStatus httpStatus
    ) {
        return RestAssured.given().log().all()
            .when().request(method, url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }
}
