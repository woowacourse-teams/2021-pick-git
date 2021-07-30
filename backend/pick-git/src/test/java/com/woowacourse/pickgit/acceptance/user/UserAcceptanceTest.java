package com.woowacourse.pickgit.acceptance.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.presentation.dto.response.ContributionResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.FollowResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.SearchResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.UserProfileResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.groups.Tuple;
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
class UserAcceptanceTest {

    @LocalServerPort
    private int port;

    @MockBean
    private OAuthClient oAuthClient;

    private String loginUserAccessToken;

    private User loginUser;
    private User targetUser;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        loginUser = UserFactory.user("testUser");
        targetUser = UserFactory.user("testUser2");

        loginUserAccessToken = 로그인_되어있음(loginUser).getToken();
        로그인_되어있음(targetUser);
    }

    @DisplayName("사용자는 자신의 프로필을 조회할 수 있다.")
    @Test
    void getAuthenticatedUserProfile_LoginUser_Success() {
        // given
        UserProfileResponseDto responseDto = UserFactory.mockLoginUserProfileResponseDto();

        // when
        UserProfileResponse response =
            authenticatedGetRequest(loginUserAccessToken, "/api/profiles/me", HttpStatus.OK)
                .as(UserProfileResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("사용자는 유효하지 않은 토큰으로 자신의 프로필을 조회할 수 없다. - 401 예외")
    @Test
    void getAuthenticatedUserProfile_LoginUserWithInvalidToken_401Exception() {
        // when
        ApiErrorResponse response =
            authenticatedGetRequest("testToken", "/api/profiles/me", HttpStatus.UNAUTHORIZED)
                .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("사용자는 토큰 없이 자신의 프로필을 조회할 수 없다. - 401 예외")
    @Test
    void getAuthenticatedUserProfile_LoginUserWithoutToken_401Exception() {
        // when
        ApiErrorResponse response =
            unauthenticatedGetRequest("/api/profiles/me", HttpStatus.UNAUTHORIZED)
                .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("사용자는 팔로우한 유저의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_LoginUserIsFollowing_Success() {
        // given
        UserProfileResponseDto responseDto =
            UserFactory.mockLoginUserProfileIsFollowingResponseDto();

        authenticatedPostRequest(loginUserAccessToken,
            String.format("/api/profiles/%s/followings", targetUser.getName()), HttpStatus.OK);

        // when
        UserProfileResponse response =
            authenticatedGetRequest(loginUserAccessToken,
                String.format("/api/profiles/%s", targetUser.getName()), HttpStatus.OK)
                .as(UserProfileResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("사용자는 팔로우하지 않은 유저의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_LoginUserIsNotFollowing_Success() {
        // given
        UserProfileResponseDto responseDto =
            UserFactory.mockLoginUserProfileIsNotFollowingResponseDto();

        // when
        UserProfileResponse response =
            authenticatedGetRequest(loginUserAccessToken,
                String.format("/api/profiles/%s", targetUser.getName()), HttpStatus.OK)
                .as(UserProfileResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("게스트는 유저의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_GuestUser_Success() {
        //given
        UserProfileResponseDto responseDto = UserFactory.mockGuestUserProfileResponseDto();

        //when
        UserProfileResponse response = unauthenticatedGetRequest(
            String.format("/api/profiles/%s", loginUser.getName()), HttpStatus.OK)
            .as(UserProfileResponse.class);

        //then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("게스트는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_GuestUser_400Exception() {
        // when
        ApiErrorResponse response = unauthenticatedGetRequest(
            String.format("/api/profiles/%s", "invalidName"), HttpStatus.BAD_REQUEST)
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("U0001");
    }

    @DisplayName("사용자는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_LoginUser_400Exception() {
        // when
        ApiErrorResponse response =
            authenticatedGetRequest(loginUserAccessToken,
                String.format("/api/profiles/%s", "invalidName"), HttpStatus.BAD_REQUEST)
                .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("U0001");
    }

    @DisplayName("누구든지 활동 통계를 조회할 수 있다.")
    @Test
    void getContributions_Anyone_Success() {
        // given
        ContributionResponseDto contributions = UserFactory.mockContributionResponseDto();

        // when
        ContributionResponse response = unauthenticatedGetRequest(
            String.format("/api/profiles/%s/contributions", "testUser"), HttpStatus.OK)
            .as(ContributionResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(contributions);
    }

    @DisplayName("유효하지 않은 유저 이름으로 활동 통계를 조회할 수 없다. - 400 예외")
    @Test
    void getContributions_invalidUsername_400Exception() {
        // when
        ApiErrorResponse response = unauthenticatedGetRequest(
            String.format("/api/profiles/%s/contributions", "invalidName"), HttpStatus.BAD_REQUEST)
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("U0001");
    }

    @DisplayName("source 유저는 target 유저를 팔로우할 수 있다.")
    @Test
    void followUser_SourceToTarget_Success() {
        // given
        FollowResponse responseDto = new FollowResponse(1, true);

        // when
        FollowResponse response =
            authenticatedPostRequest(loginUserAccessToken,
                String.format("/api/profiles/%s/followings", targetUser.getName()), HttpStatus.OK)
                .as(FollowResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("source 유저와 target 유저가 동일하면 팔로우할 수 없다. - 400 예외")
    @Test
    void followUser_SameSourceToSameTarget_400Exception() {
        // when
        ApiErrorResponse response =
            authenticatedPostRequest(loginUserAccessToken,
                String.format("/api/profiles/%s/followings", loginUser.getName()),
                HttpStatus.BAD_REQUEST).as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("U0004");
    }

    @DisplayName("source 유저는 존재하지 않는 유저를 팔로우할 수 없다. - 400 예외")
    @Test
    void followUser_SourceToInvalidTarget_400Exception() {
        // when
        ApiErrorResponse response =
            authenticatedPostRequest(loginUserAccessToken,
                String.format("/api/profiles/%s/followings", "invalidName"), HttpStatus.BAD_REQUEST)
                .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("U0001");
    }

    @DisplayName("source 유저는 이미 팔로우한 유저를 팔로우할 수 없다. - 400 예외")
    @Test
    void followUser_SourceToExistingTarget_400Exception() {
        // given
        FollowResponse followResponse =
            authenticatedPostRequest(loginUserAccessToken,
                String.format("/api/profiles/%s/followings", targetUser.getName()), HttpStatus.OK)
                .as(FollowResponse.class);

        FollowResponse expectedResponse = new FollowResponse(1, true);

        assertThat(followResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse);

        // when
        ApiErrorResponse errorResponse =
            authenticatedPostRequest(loginUserAccessToken,
                String.format("/api/profiles/%s/followings", targetUser.getName()),
                HttpStatus.BAD_REQUEST).as(ApiErrorResponse.class);

        // then
        assertThat(errorResponse.getErrorCode()).isEqualTo("U0002");
    }

    @DisplayName("source 유저는 target 유저를 언팔로우할 수 있다.")
    @Test
    void unfollowUser_SourceToTarget_Success() {
        // given
        FollowResponse followResponse =
            authenticatedPostRequest(loginUserAccessToken,
                String.format("/api/profiles/%s/followings", targetUser.getName()), HttpStatus.OK)
                .as(FollowResponse.class);

        FollowResponse followExpectedResponse = new FollowResponse(1, true);

        assertThat(followResponse)
            .usingRecursiveComparison()
            .isEqualTo(followExpectedResponse);

        FollowResponse unfollowExpectedResponse = new FollowResponse(0, false);

        // when
        FollowResponse unfollowResponse =
            authenticatedDeleteRequest(loginUserAccessToken,
                String.format("/api/profiles/%s/followings", targetUser.getName()), HttpStatus.OK)
                .as(FollowResponse.class);

        // then
        assertThat(unfollowResponse)
            .usingRecursiveComparison()
            .isEqualTo(unfollowExpectedResponse);
    }

    @DisplayName("source 유저와 target 유저가 동일하면 언팔로우할 수 없다. - 400 예외")
    @Test
    void unfollowUser_SameSourceToSameTarget_400Exception() {
        // when
        ApiErrorResponse response =
            authenticatedDeleteRequest(loginUserAccessToken,
                String.format("/api/profiles/%s/followings", loginUser.getName()),
                HttpStatus.BAD_REQUEST).as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("U0004");
    }

    @DisplayName("source 유저는 존재하지 않는 유저를 언팔로우할 수 없다. - 400 예외")
    @Test
    void unfollowUser_SourceToInvalidTarget_400Exception() {
        // when
        ApiErrorResponse response =
            authenticatedDeleteRequest(loginUserAccessToken,
                String.format("/api/profiles/%s/followings", "invalidName"), HttpStatus.BAD_REQUEST)
                .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("U0001");
    }

    @DisplayName("source 유저는 존재하지 않는 팔로우에 대해 언팔로우할 수 없다. - 400 예외")
    @Test
    void unfollowUser_NotExistingFollow_ExceptionThrown() {
        // when
        ApiErrorResponse response =
            authenticatedDeleteRequest(loginUserAccessToken,
                String.format("/api/profiles/%s/followings", targetUser.getName()),
                HttpStatus.BAD_REQUEST).as(ApiErrorResponse.class);

        //then
        assertThat(response.getErrorCode()).isEqualTo("U0003");
    }

    @DisplayName("로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색할 수 있다. 단, 자기 자신은 검색되지 않는다.(팔로잉 여부 true/false)")
    @Test
    void searchUser_LoginUser_Success() {
        // given
        authenticatedPostRequest(loginUserAccessToken,
            String.format("/api/profiles/%s/followings", targetUser.getName()), HttpStatus.OK);
        User unfollweredUser = UserFactory.user("testUser3");
        로그인_되어있음(unfollweredUser);

        // when
        String url = String.format("/api/search?keyword=%s&page=0&limit=5", "testUser");
        SearchResponse response = authenticatedGetRequest(loginUserAccessToken, url, HttpStatus.OK)
            .as(SearchResponse.class);

        // then
        assertThat(response.getUsers()).hasSize(2);
        assertThat(response.getUsers())
            .extracting("username", "following")
            .containsExactly(
                tuple(targetUser.getName(), true),
                tuple(unfollweredUser.getName(), false)
            );
    }

    @DisplayName("비 로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색할 수 있다. (팔로잉 필드 null)")
    @Test
    void searchUser_GuestUser_Success() {
        // when
        String url = String.format("/api/search?keyword=%s&page=0&limit=5", "testUser");
        SearchResponse response = unauthenticatedGetRequest(url, HttpStatus.OK)
            .as(SearchResponse.class);

        // then
        assertThat(response.getUsers()).hasSize(2);
        assertThat(response.getUsers())
            .extracting("username", "following")
            .containsExactly(
                tuple(loginUser.getName(), null),
                tuple(targetUser.getName(), null)
            );
    }

    private ExtractableResponse<Response> authenticatedGetRequest(
        String accessToken,
        String url,
        HttpStatus httpStatus) {
        return RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .when().get(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    private ExtractableResponse<Response> unauthenticatedGetRequest(
        String url,
        HttpStatus httpStatus) {
        return RestAssured.given().log().all()
            .when().get(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    private ExtractableResponse<Response> authenticatedPostRequest(
        String accessToken,
        String url,
        HttpStatus httpStatus) {
        return RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .when().post(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    private ExtractableResponse<Response> authenticatedDeleteRequest(
        String accessToken,
        String url,
        HttpStatus httpStatus) {
        return RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .when().delete(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    private OAuthTokenResponse 로그인_되어있음(User user) {
        OAuthTokenResponse response = 로그인_요청(user).as(OAuthTokenResponse.class);
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
