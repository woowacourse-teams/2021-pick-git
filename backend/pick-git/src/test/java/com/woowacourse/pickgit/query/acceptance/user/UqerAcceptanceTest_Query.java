package com.woowacourse.pickgit.query.acceptance.user;

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

public class UqerAcceptanceTest_Query extends AcceptanceTest {

    @MockBean
    private OAuthClient oAuthClient;

    private String loginUserAccessToken;

    private User loginUser;

    private User targetUser;

    @BeforeEach
    void setUp() {
        //given
        loginUser = UserFactory.user("testUser");
        targetUser = UserFactory.user("testUser2");

        loginUserAccessToken = 로그인_되어있음(loginUser).getToken();
        로그인_되어있음(targetUser);
    }

    @DisplayName("로그인된 사용자는 자신의 프로필을 조회할 수 있다.")
    @Test
    void getAuthenticatedUserProfile_LoginUser_Success() {
        // given
        UserProfileResponseDto responseDto = UserFactory.mockLoginUserProfileResponseDto();

        // when
        UserProfileResponse response =
            authenticatedRequest(
                loginUserAccessToken,
                "/api/profiles/me",
                Method.GET,
                HttpStatus.OK
            ).as(UserProfileResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
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
        // given
        UserProfileResponseDto responseDto =
            UserFactory.mockLoginUserProfileIsFollowingResponseDto();

        authenticatedRequest(
            loginUserAccessToken,
            String.format("/api/profiles/%s/followings?githubFollowing=false",
                targetUser.getName()),
            Method.POST,
            HttpStatus.OK
        );

        // when
        UserProfileResponse response =
            authenticatedRequest(
                loginUserAccessToken,
                String.format("/api/profiles/%s", targetUser.getName()),
                Method.GET,
                HttpStatus.OK
            ).as(UserProfileResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("로그인된 사용자는 팔로우하지 않은 유저의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_LoginUserIsNotFollowing_Success() {
        // given
        UserProfileResponseDto responseDto =
            UserFactory.mockLoginUserProfileIsNotFollowingResponseDto();

        // when
        UserProfileResponse response =
            authenticatedRequest(
                loginUserAccessToken,
                String.format("/api/profiles/%s", targetUser.getName()),
                Method.GET,
                HttpStatus.OK
            ).as(UserProfileResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("로그인된 사용자는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_LoginUser_400Exception() {
        // when
        ApiErrorResponse response =
            authenticatedRequest(
                loginUserAccessToken,
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
        UserProfileResponseDto responseDto = UserFactory.mockGuestUserProfileResponseDto();

        //when
        UserProfileResponse response = unauthenticatedRequest(
            String.format("/api/profiles/%s", loginUser.getName()),
            Method.GET,
            HttpStatus.OK
        ).as(UserProfileResponse.class);

        //then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
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

    private OAuthTokenResponse 로그인_되어있음(User user) {
        // when
        OAuthTokenResponse response = 로그인_요청(user).as(OAuthTokenResponse.class);

        // then
        assertThat(response.getToken()).isNotBlank();

        return response;
    }

    private ExtractableResponse<Response> 로그인_요청(User user) {
        // given
        String oauthCode = "1234";
        String accessToken = "oauth.access.token";

        OAuthProfileResponse oAuthProfileResponse = new OAuthProfileResponse(
            user.getName(), user.getImage(), user.getDescription(), user.getGithubUrl(),
            user.getCompany(), user.getLocation(), user.getWebsite(), user.getTwitter()
        );

        given(oAuthClient.getAccessToken(oauthCode))
            .willReturn(accessToken);
        given(oAuthClient.getGithubProfile(accessToken))
            .willReturn(oAuthProfileResponse);

        // then
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
