package com.woowacourse.pickgit.acceptance.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.TestApiConfiguration;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.presentation.dto.FollowResponse;
import com.woowacourse.pickgit.user.presentation.dto.UserProfileResponse;
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

@Import(TestApiConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class UserAcceptanceTest {

    private static final String SOURCE_USER_NAME = "yjksw";
    private static final String TARGET_USER_NAME = "pickgit";

    @LocalServerPort
    private int port;

    @MockBean
    private OAuthClient oAuthClient;

    private UserFactory userFactory = new UserFactory();

    private String userAccessToken;

    private String anotherAccessToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        userAccessToken = 로그인_되어있음(userFactory.user()).getToken();
        anotherAccessToken = 로그인_되어있음(userFactory.anotherUser()).getToken();
    }


    @DisplayName("본인의 프로필 조회에 성공한다.")
    @Test
    void getAuthenticatedUserProfile_ValidUser_Success() {
        //given
        User user = userFactory.user();
        String requestUrl = "/api/profiles/me";
        UserProfileResponse expectedResponseDto =
            new UserProfileResponse(user.getName(), user.getImage(), user.getDescription(),
                user.getFollowerCount(), user.getFollowingCount(), user.getPostCount(),
                user.getGithubUrl(), user.getCompany(), user.getLocation(), user.getWebsite(),
                user.getTwitter(), null);

        //when
        UserProfileResponse actualResponseDto =
            authenticatedGetRequest(userAccessToken, requestUrl, HttpStatus.OK)
                .as(UserProfileResponse.class);

        //then
        assertThat(actualResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponseDto);
    }

    @DisplayName("본인의 프로필 조회시 토큰이 없으면 예외를 발생시킨다.")
    @Test
    void getAuthenticatedUserProfile_noToken_ExceptionThrown() {
        //given
        String requestUrl = "/api/profiles/me";

        //when
        //then
        unauthenticatedGetRequest(requestUrl, HttpStatus.UNAUTHORIZED);
    }

    @DisplayName("로그인 상태에서 팔로우하는 타인의 프로필 조회에 성공한다.")
    @Test
    void getUserProfile_ValidLoginUserFollowing_Success() {
        //given
        User targetUser = userFactory.anotherUser();

        String followRequestUrl = "/api/profiles/" + targetUser.getName() + "/followings";
        String requestUrl = "/api/profiles/" + targetUser.getName();
        UserProfileResponse expectedResponseDto =
            new UserProfileResponse(targetUser.getName(), targetUser.getImage(), targetUser.getDescription(),
                1, targetUser.getFollowingCount(), targetUser.getPostCount(),
                targetUser.getGithubUrl(), targetUser.getCompany(), targetUser.getLocation(), targetUser.getWebsite(),
                targetUser.getTwitter(), true);

        authenticatedPostRequest(userAccessToken, followRequestUrl, HttpStatus.OK);

        //when
        UserProfileResponse actualResponseDto =
            authenticatedGetRequest(userAccessToken, requestUrl, HttpStatus.OK)
                .as(UserProfileResponse.class);

        //then
        assertThat(actualResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponseDto);
    }

    @DisplayName("로그인 상태에서 팔로우하지 않는 타인의 프로필 조회에 성공한다.")
    @Test
    void getUserProfile_ValidLoginUserUnfollowing_Success() {
        //given
        User user = userFactory.anotherUser();
        String requestUrl = "/api/profiles/" + user.getName();
        UserProfileResponse expectedResponseDto =
            new UserProfileResponse(user.getName(), user.getImage(), user.getDescription(),
                user.getFollowerCount(), user.getFollowingCount(), user.getPostCount(),
                user.getGithubUrl(), user.getCompany(), user.getLocation(), user.getWebsite(),
                user.getTwitter(), false);

        //when
        UserProfileResponse actualResponseDto =
            authenticatedGetRequest(userAccessToken, requestUrl, HttpStatus.OK)
                .as(UserProfileResponse.class);

        //then
        assertThat(actualResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponseDto);
    }

    @DisplayName("비로그인 상태에서 타인의 프로필 조회에 성공한다.")
    @Test
    void getUserProfile_ValidGuestUser_Success() {
        //given
        User user = userFactory.anotherUser();
        String requestUrl = "/api/profiles/" + user.getName();
        UserProfileResponse expectedResponseDto =
            new UserProfileResponse(user.getName(), user.getImage(), user.getDescription(),
                user.getFollowerCount(), user.getFollowingCount(), user.getPostCount(),
                user.getGithubUrl(), user.getCompany(), user.getLocation(), user.getWebsite(),
                user.getTwitter(), null);

        //when
        UserProfileResponse actualResponseDto =
            unauthenticatedGetRequest(requestUrl, HttpStatus.OK)
                .as(UserProfileResponse.class);

        //then
        assertThat(actualResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponseDto);
    }

    @DisplayName("한 로그인 유저가 다른 유저를 팔로우하는데 성공한다.")
    @Test
    void followUser_ValidUser_Success() {
        //given
        User user = userFactory.anotherUser();
        String requestUrl = "/api/profiles/" + user.getName() + "/followings";
        FollowResponse expectedResponseDto = new FollowResponse(1, true);

        //when
        FollowResponse actualResponseDto =
            authenticatedPostRequest(userAccessToken, requestUrl, HttpStatus.OK)
                .as(FollowResponse.class);

        //then
        assertThat(actualResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponseDto);
    }

    @DisplayName("같은 source와 target이 팔로우 요청을 하면 예외가 발생한다.")
    @Test
    void followUser_SameSourceTargetUser_ExceptionThrown() {
        //given
        String requestUrl = "/api/profiles/" + SOURCE_USER_NAME + "/followings";

        //when
        //then
        authenticatedPostRequest(userAccessToken, requestUrl, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("한 로그인 유저가 없는 유저를 팔로우하면 예외가 발생한다.")
    @Test
    void followUser_InvalidTargetUser_ExceptionThrown() {
        //given
        String requestUrl = "/api/profiles/" + "invalidUser" + "/followings";

        //when
        //then
        authenticatedPostRequest(userAccessToken, requestUrl, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("이미 존재하는 팔로우 요청 시 예외가 발생한다.")
    @Test
    void followUser_ExistingFollow_ExceptionThrown() {
        //given
        User user = userFactory.anotherUser();
        String requestUrl = "/api/profiles/" + user.getName() + "/followings";
        FollowResponse followResponse = authenticatedPostRequest(
            userAccessToken, requestUrl, HttpStatus.OK)
            .as(FollowResponse.class);

        FollowResponse followExpectedResponseDto = new FollowResponse(1, true);

        assertThat(followResponse)
            .usingRecursiveComparison()
            .isEqualTo(followExpectedResponseDto);

        //when
        //then
        authenticatedPostRequest(userAccessToken, requestUrl, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("한 로그인 유저가 다른 유저를 언팔로우하는데 성공한다.")
    @Test
    void unfollowUser_ValidUser_Success() {
        //given
        User user = userFactory.anotherUser();
        String requestUrl = "/api/profiles/" + user.getName() + "/followings";
        FollowResponse followResponse = authenticatedPostRequest(
            userAccessToken, requestUrl, HttpStatus.OK)
            .as(FollowResponse.class);

        FollowResponse followExpectedResponseDto = new FollowResponse(1, true);
        FollowResponse unfollowExpectedResponseDto = new FollowResponse(0, false);

        assertThat(followResponse)
            .usingRecursiveComparison()
            .isEqualTo(followExpectedResponseDto);

        //when
        FollowResponse actualResponseDto =
            authenticatedDeleteRequest(userAccessToken, requestUrl, HttpStatus.OK)
                .as(FollowResponse.class);

        //then
        assertThat(actualResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(unfollowExpectedResponseDto);
    }

    @DisplayName("같은 source와 target이 팔로우 요청을 하면 예외가 발생한다.")
    @Test
    void unfollowUser_SameSourceTargetUser_ExceptionThrown() {
        //given
        String requestUrl = "/api/profiles/" + SOURCE_USER_NAME + "/followings";

        //when
        //then
        authenticatedDeleteRequest(userAccessToken, requestUrl, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("한 로그인 유저가 없는 유저를 언팔로우하면 예외가 발생한다.")
    @Test
    void unfollowUser_InvalidTargetUser_ExceptionThrown() {
        //given
        String requestUrl = "/api/profiles/" + "invalidUser" + "/followings";

        //when
        //then
        authenticatedDeleteRequest(userAccessToken, requestUrl, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("존재하지 않는 팔로우 관계에 대한 언팔로우 요청 시 예외가 발생한다.")
    @Test
    void unfollowUser_NotExistingFollow_ExceptionThrown() {
        //given
        User user = userFactory.anotherUser();
        String requestUrl = "/api/profiles/" + user.getName() + "/followings";

        //when
        //then
        authenticatedDeleteRequest(userAccessToken, requestUrl, HttpStatus.BAD_REQUEST);
    }

    private ExtractableResponse<Response> authenticatedGetRequest(String accessToken, String url,
        HttpStatus httpStatus) {
        return RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .when().get(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    private ExtractableResponse<Response> unauthenticatedGetRequest(String url,
        HttpStatus httpStatus) {
        return RestAssured.given().log().all()
            .when().get(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    private ExtractableResponse<Response> authenticatedPostRequest(String accessToken, String url,
        HttpStatus httpStatus) {
        return RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .when().post(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    private ExtractableResponse<Response> authenticatedDeleteRequest(String accessToken, String url,
        HttpStatus httpStatus) {
        return RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .when().delete(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    public OAuthTokenResponse 로그인_되어있음(User user) {
        OAuthTokenResponse response = 로그인_요청(user).as(OAuthTokenResponse.class);
        assertThat(response.getToken()).isNotBlank();
        return response;
    }

    public ExtractableResponse<Response> 로그인_요청(User user) {
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
