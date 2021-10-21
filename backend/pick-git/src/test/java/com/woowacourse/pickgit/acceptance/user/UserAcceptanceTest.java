package com.woowacourse.pickgit.acceptance.user;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.BDDMockito.given;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.presentation.dto.request.ProfileDescriptionRequest;
import com.woowacourse.pickgit.user.presentation.dto.response.ContributionResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.FollowResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.ProfileDescriptionResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.ProfileImageEditResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.UserProfileResponse;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class UserAcceptanceTest extends AcceptanceTest {

    private String loginUserAccessToken;

    private User loginUser;

    private User targetUser;

    @BeforeEach
    void setUp() {
        //given
        loginUser = UserFactory.user("testUser");
        targetUser = UserFactory.user("testUser2");

        loginUserAccessToken = 로그인_되어있음(loginUser.getName()).getToken();
        로그인_되어있음(targetUser.getName());
    }

    @DisplayName("로그인되지 않은 사용자는 target 유저를 팔로우 할 수 없다.")
    @Test
    void followUser_NotLogin_Failure() {
        ApiErrorResponse response = unauthenticatedRequest(
            String.format("/api/profiles/%s/followings?githubFollowing=false", targetUser.getName()),
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
                String.format("/api/profiles/%s/followings?githubFollowing=false",
                    targetUser.getName()),
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
                String.format("/api/profiles/%s/followings?githubFollowing=false",
                    loginUser.getName()),
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
                String.format("/api/profiles/%s/followings?githubFollowing=false",
                    "invalidName"),
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
                String.format("/api/profiles/%s/followings?githubFollowing=false",
                    targetUser.getName()),
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
                String.format("/api/profiles/%s/followings?githubFollowing=false", targetUser.getName()),
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
            String.format("/api/profiles/%s/followings?githubUnfollowing=false",
                targetUser.getName()),
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
                String.format("/api/profiles/%s/followings?githubFollowing=false", targetUser.getName()),
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
                String.format("/api/profiles/%s/followings?githubUnfollowing=false", targetUser.getName()),
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
                String.format("/api/profiles/%s/followings?githubUnfollowing=false",
                    loginUser.getName()),
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
                String.format("/api/profiles/%s/followings?githubUnfollowing=false",
                    "invalidName"),
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
                String.format("/api/profiles/%s/followings?githubUnfollowing=false",
                    targetUser.getName()),
                Method.DELETE,
                HttpStatus.BAD_REQUEST
            ).as(ApiErrorResponse.class);

        //then
        assertThat(response.getErrorCode()).isEqualTo("U0003");
    }

    @DisplayName("로그인 사용자는 자신의 프로필 이미지를 수정할 수 있다.")
    @Test
    void editProfileImage_LoginUser_Success() throws IOException {
        // given
        File imageFile = FileFactory.getTestImage1File();

        // when
        ProfileImageEditResponse response = given().log().all()
            .auth().oauth2(loginUserAccessToken)
            .contentType(MediaType.TEXT_PLAIN_VALUE)
            .body(new FileInputStream(imageFile).readAllBytes())
            .when()
            .put("/api/profiles/me/image")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(ProfileImageEditResponse.class);

        // then
        assertThat(response.getImageUrl()).isNotBlank();
    }

    @DisplayName("게스트는 프로필 이미지를 수정할 수 없다.")
    @Test
    void editProfileImage_GuestUser_Fail() throws IOException {
        // given
        File imageFile = FileFactory.getTestImage1File();

        // when
        ApiErrorResponse response = given().log().all()
            .body(new FileInputStream(imageFile).readAllBytes())
            .when()
            .put("/api/profiles/me/image")
            .then().log().all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .extract()
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("로그인 사용자는 자신의 한 줄 소개를 수정할 수 있다.")
    @Test
    void editProfileDescription_LoginUser_Success() {
        // given
        String description = "updated description";

        // when
        ProfileDescriptionResponse response = given().log().all()
            .auth().oauth2(loginUserAccessToken)
            .contentType(ContentType.JSON)
            .body(new ProfileDescriptionRequest(description))
            .when()
            .put("/api/profiles/me/description")
            .then().log().all()
            .extract()
            .as(ProfileDescriptionResponse.class);

        // then
        assertThat(response.getDescription()).isEqualTo(description);
    }

    @DisplayName("게스트는 자신의 한 줄 소개를 수정할 수 없다.")
    @Test
    void editProfileDescription_GuestUser_Fail() {
        // given
        String description = "updated description";

        // when
        ApiErrorResponse response = given().log().all()
            .contentType(ContentType.JSON)
            .body(new ProfileDescriptionRequest(description))
            .when()
            .put("/api/profiles/me/description")
            .then().log().all()
            .extract()
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색할 수 있다. 단, 자기 자신은 검색되지 않는다.(팔로잉 여부 true/false)")
    @Test
    void searchUser_LoginUser_Success() {
        // given
        authenticatedRequest(
            loginUserAccessToken,
            String.format("/api/profiles/%s/followings?githubFollowing=false", targetUser.getName()),
            Method.POST,
            HttpStatus.OK
        );
        User unfollowedUser = UserFactory.user("testUser3");
        로그인_되어있음(unfollowedUser.getName());

        // when
        String url = String.format("/api/search/users?keyword=%s&page=0&limit=5", "testUser");
        List<UserSearchResponseDto> response =
            authenticatedRequest(
                loginUserAccessToken,
                url,
                Method.GET,
                HttpStatus.OK
            ).as(new TypeRef<>() {
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
            ).as(new TypeRef<>() {
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

    @DisplayName("유효하지 않은 토큰으로 활동 통계를 조회할 수 없다. - 401 예외")
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
}
