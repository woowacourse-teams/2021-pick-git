package com.woowacourse.pickgit.acceptance.user;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.presentation.dto.response.ContributionResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.FollowResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.ProfileEditResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.UserProfileResponse;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
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
            String.format("/api/profiles/%s/followings", targetUser.getName()),
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

    @DisplayName("로그인되지 않은 사용자는 target 유저를 팔로우 할 수 없다.")
    @Test
    void followUser_NotLogin_Failure() {
        ApiErrorResponse response = unauthenticatedRequest(
            String.format("/api/profiles/%s/followings", targetUser.getName()),
            Method.POST,
            HttpStatus.UNAUTHORIZED
        ).as(ApiErrorResponse.class);

        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("팔로우중이지 않다면 source 유저는 target 유저를 팔로우할 수 있다.")
    @Test
    void followUser_SourceToTarget_Success() {
        // given
        FollowResponse responseDto = new FollowResponse(1, true);

        // when
        FollowResponse response =
            authenticatedRequest(
                loginUserAccessToken,
                String.format("/api/profiles/%s/followings", targetUser.getName()),
                Method.POST,
                HttpStatus.OK
            ).as(FollowResponse.class);

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
            authenticatedRequest(
                loginUserAccessToken,
                String.format("/api/profiles/%s/followings", loginUser.getName()),
                Method.POST,
                HttpStatus.BAD_REQUEST
            ).as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("U0004");
    }

    @DisplayName("source 유저는 존재하지 않는 유저를 팔로우할 수 없다. - 400 예외")
    @Test
    void followUser_SourceToInvalidTarget_400Exception() {
        // when
        ApiErrorResponse response =
            authenticatedRequest(
                loginUserAccessToken,
                String.format("/api/profiles/%s/followings", "invalidName"),
                Method.POST,
                HttpStatus.BAD_REQUEST
            ).as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("U0001");
    }

    @DisplayName("source 유저는 이미 팔로우한 유저를 팔로우할 수 없다. - 400 예외")
    @Test
    void followUser_SourceToExistingTarget_400Exception() {
        // given
        FollowResponse followResponse =
            authenticatedRequest(
                loginUserAccessToken,
                String.format("/api/profiles/%s/followings", targetUser.getName()),
                Method.POST,
                HttpStatus.OK
            ).as(FollowResponse.class);

        FollowResponse expectedResponse = new FollowResponse(1, true);

        assertThat(followResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse);

        // when
        ApiErrorResponse errorResponse =
            authenticatedRequest(
                loginUserAccessToken,
                String.format("/api/profiles/%s/followings", targetUser.getName()),
                Method.POST,
                HttpStatus.BAD_REQUEST
            ).as(ApiErrorResponse.class);

        // then
        assertThat(errorResponse.getErrorCode()).isEqualTo("U0002");
    }

    @DisplayName("로그인되지 않은 사용자는 target 유저를 언팔로우 할 수 없다.")
    @Test
    void unfollowUser_NotLogin_Failure() {
        ApiErrorResponse response = unauthenticatedRequest(
            String.format("/api/profiles/%s/followings", targetUser.getName()),
            Method.DELETE,
            HttpStatus.UNAUTHORIZED
        ).as(ApiErrorResponse.class);

        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("source 유저는 target 유저를 언팔로우할 수 있다.")
    @Test
    void unfollowUser_SourceToTarget_Success() {
        // given
        FollowResponse followResponse =
            authenticatedRequest(
                loginUserAccessToken,
                String.format("/api/profiles/%s/followings", targetUser.getName()),
                Method.POST,
                HttpStatus.OK
            ).as(FollowResponse.class);

        FollowResponse followExpectedResponse = new FollowResponse(1, true);

        assertThat(followResponse)
            .usingRecursiveComparison()
            .isEqualTo(followExpectedResponse);

        FollowResponse unfollowExpectedResponse = new FollowResponse(0, false);

        // when
        FollowResponse unfollowResponse =
            authenticatedRequest(
                loginUserAccessToken,
                String.format("/api/profiles/%s/followings", targetUser.getName()),
                Method.DELETE,
                HttpStatus.OK
            ).as(FollowResponse.class);

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
            authenticatedRequest(
                loginUserAccessToken,
                String.format("/api/profiles/%s/followings", loginUser.getName()),
                Method.DELETE,
                HttpStatus.BAD_REQUEST
            ).as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("U0004");
    }

    @DisplayName("source 유저는 존재하지 않는 유저를 언팔로우할 수 없다. - 400 예외")
    @Test
    void unfollowUser_SourceToInvalidTarget_400Exception() {
        // when
        ApiErrorResponse response =
            authenticatedRequest(
                loginUserAccessToken,
                String.format("/api/profiles/%s/followings", "invalidName"),
                Method.DELETE,
                HttpStatus.BAD_REQUEST
            ).as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("U0001");
    }

    @DisplayName("source 유저는 팔로우하지 않는 유저를 언팔로우할 수 없다. - 400 예외")
    @Test
    void unfollowUser_NotExistingFollow_ExceptionThrown() {
        // when
        ApiErrorResponse response =
            authenticatedRequest(
                loginUserAccessToken,
                String.format("/api/profiles/%s/followings", targetUser.getName()),
                Method.DELETE,
                HttpStatus.BAD_REQUEST
            ).as(ApiErrorResponse.class);

        //then
        assertThat(response.getErrorCode()).isEqualTo("U0003");
    }

    @DisplayName("로그인 사용자는 자신의 프로필(이미지, 한 줄 소개 포함)을 수정할 수 있다.")
    @Test
    void editUserProfile_LoginUserWithImageAndDescription_Success() {
        // given
        String description = "updated profile description";
        File imageFile = FileFactory.getTestImage1File();

        // when
        ProfileEditResponse response = given().log().all()
            .auth().oauth2(loginUserAccessToken)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .formParams("description", description)
            .multiPart("image", imageFile)
            .when()
            .post("/api/profiles/me")
            .then().log().all()
            .extract()
            .as(ProfileEditResponse.class);

        // then
        assertThat(response.getImageUrl()).isNotBlank();
        assertThat(response.getDescription()).isEqualTo(description);
    }

    @DisplayName("게스트는 프로필을 수정할 수 없다.")
    @Test
    void editUserProfile_GuestUser_Fail() {
        // given
        String description = "updated profile description";

        // when
        ApiErrorResponse response = given().log().all()
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .formParams("description", description)
            .multiPart("image", "")
            .when()
            .post("/api/profiles/me")
            .then().log().all()
            .extract()
            .as(ApiErrorResponse.class);

        // then
        assertThat(response)
            .extracting("errorCode")
            .isEqualTo("A0001");
    }

    @DisplayName("로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색할 수 있다. 단, 자기 자신은 검색되지 않는다.(팔로잉 여부 true/false)")
    @Test
    void searchUser_LoginUser_Success() {
        // given
        authenticatedRequest(
            loginUserAccessToken,
            String.format("/api/profiles/%s/followings", targetUser.getName()),
            Method.POST,
            HttpStatus.OK
        );
        User unfollowedUser = UserFactory.user("testUser3");
        로그인_되어있음(unfollowedUser);

        // when
        String url = String.format("/api/search/users?keyword=%s&page=0&limit=5", "testUser");
        List<UserSearchResponseDto> response =
            authenticatedRequest(
                loginUserAccessToken,
                url,
                Method.GET,
                HttpStatus.OK
            ).as(new TypeRef<List<UserSearchResponseDto>>() {
                });

        // then
        assertThat(response)
            .hasSize(2)
            .extracting("username", "following")
            .containsExactly(
                tuple(targetUser.getName(), true),
                tuple(unfollowedUser.getName(), false)
            );
    }

    @DisplayName("비 로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색할 수 있다. (팔로잉 필드 null)")
    @Test
    void searchUser_GuestUser_Success() {
        // when
        String url = String.format("/api/search/users?keyword=%s&page=0&limit=5", "testUser");
        List<UserSearchResponseDto> response =
            unauthenticatedRequest(
                url,
                Method.GET,
                HttpStatus.OK
            ).as(new TypeRef<List<UserSearchResponseDto>>() {
                });

        // then
        assertThat(response)
            .hasSize(2)
            .extracting("username", "following")
            .containsExactly(
                tuple(loginUser.getName(), null),
                tuple(targetUser.getName(), null)
            );
    }

    @DisplayName("사용자는 활동 통계를 조회할 수 있다.")
    @Test
    void getContributions_LoginUser_Success() {
        // given
        ContributionResponseDto contributions = UserFactory.mockContributionResponseDto();

        // when
        ContributionResponse response = authenticatedRequest(
            loginUserAccessToken,
            String.format("/api/profiles/%s/contributions", "testUser"),
            Method.GET,
            HttpStatus.OK
        ).as(ContributionResponse.class);

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(contributions);
    }

    @DisplayName("게스트는 활동 통계를 조회할 수 없다. - 401 예외")
    @Test
    void getContributions_invalidToken_401Exception() {
        // when
        ApiErrorResponse response = authenticatedRequest(
            "invalid" + loginUserAccessToken,
            String.format("/api/profiles/%s/contributions", "testUser"),
            Method.GET,
            HttpStatus.UNAUTHORIZED
        ).as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("유효하지 않은 유저 이름으로 활동 통계를 조회할 수 없다. - 400 예외")
    @Test
    void getContributions_invalidUsername_400Exception() {
        // when
        ApiErrorResponse response = authenticatedRequest(
            loginUserAccessToken,
            String.format("/api/profiles/%s/contributions", "invalidUser"),
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
