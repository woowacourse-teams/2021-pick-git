package com.woowacourse.pickgit.acceptance.post;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeUsersResponse;
import com.woowacourse.pickgit.user.domain.User;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class PostAcceptanceTest_LikeUsers extends AcceptanceTest {

    @MockBean
    private OAuthClient oAuthClient;

    @DisplayName("특정 게시물을 좋아요한 계정 리스트를 조회할 수 있다 - 로그인/성공")
    @Test
    void searchLikeUsers_LoginUser_Success() {
        // given
        Long postId = 1L;
        String authorToken = 로그인_되어있음("author").getToken();
        List<User> likeUsers = UserFactory.mockLikeUsersIncludingAuthor();

        writePost(authorToken);
        likePosts(postId, likeUsers);
        followSomeUsers(authorToken, likeUsers);

        List<LikeUsersResponse> expectedResponse = createLikeUserResponseForLoginUser(likeUsers);

        // when
        List<LikeUsersResponse> actualResponse =
            requestLikeUsersForLoginUser(authorToken, postId)
                .as(new TypeRef<>() {});

        // then
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse);
    }

    private ExtractableResponse<Response> requestLikeUsersForLoginUser(String token, Long postId) {
        return RestAssured.given().log().all()
            .auth().oauth2(token)
            .when()
            .get("/api/posts/{postId}/likes", postId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }

    private List<LikeUsersResponse> createLikeUserResponseForLoginUser(List<User> likeUsers) {
        return likeUsers.stream()
            .map(user -> {
                    if (isFollowingUser(likeUsers, user)) {
                        return new LikeUsersResponse(user.getImage(), user.getName(), true);
                    }

                    if (user.getName().equals("author")) {
                        return new LikeUsersResponse(user.getImage(), user.getName(), null);
                    }

                    return new LikeUsersResponse(user.getImage(), user.getName(), false);
                }
            ).collect(toList());
    }

    private boolean isFollowingUser(List<User> likeUsers, User user) {
        return user.getName().equals(likeUsers.get(0).getName())
            || user.getName().equals(likeUsers.get(1).getName());
    }

    @DisplayName("특정 게시물을 좋아요한 계정 리스트를 조회할 수 있다 - 비 로그인/성공")
    @Test
    void searchLikeUsers_GuestUser_Success() {
        // given
        Long postId = 1L;
        String authorToken = 로그인_되어있음("author").getToken();
        List<User> likeUsers = UserFactory.mockLikeUsersIncludingAuthor();

        writePost(authorToken);
        likePosts(postId, likeUsers);

        List<LikeUsersResponse> expectedResponse = createLikeUserResponseForGuest(likeUsers);

        // when
        List<LikeUsersResponse> actualResponse =
            requestLikeUsersForGuest(postId)
                .as(new TypeRef<>() {});

        // then
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse);
    }

    private List<LikeUsersResponse> createLikeUserResponseForGuest(List<User> likeUsers) {
        return likeUsers.stream()
            .map(
                user -> new LikeUsersResponse(user.getImage(), user.getName(), null)
            ).collect(toList());
    }

    @DisplayName("좋아요가 없는 게시물을 조회하면 빈 배열을 반환한다. - 비 로그인/성공")
    @Test
    void searchLikeUsers_EmptyLikes_Success() {
        // given
        Long postId = 1L;
        String authorToken = 로그인_되어있음("author").getToken();

        writePost(authorToken);

        // when
        List<LikeUsersResponse> actualResponse =
            requestLikeUsersForGuest(postId)
                .as(new TypeRef<>() {});

        // then
        assertThat(actualResponse).isEmpty();
    }

    private ExtractableResponse<Response> requestLikeUsersForGuest(Long postId) {
        return RestAssured.given().log().all()
            .when()
            .get("/api/posts/{postId}/likes", postId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }

    private void writePost(String token) {
        Map<String, Object> request = new HashMap<>();
        request.put("githubRepoUrl", "https://github.com/woowacourse-teams/2021-pick-git");
        request.put("tags", List.of("java", "spring"));
        request.put("content", "this is content");

        RestAssured.given().log().all()
            .auth().oauth2(token)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .formParams(request)
            .multiPart("images", FileFactory.getTestImage1File())
            .multiPart("images", FileFactory.getTestImage2File())
            .when()
            .post("/api/posts")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value());
    }

    private void likePosts(Long postId, List<User> likeUsers) {
        likeUsers.forEach(
            user -> {
                String token = 로그인_되어있음(user.getName()).getToken();
                likePost(token, postId);
            }
        );
    }

    private void likePost(String token, Long postId) {
        RestAssured.given().log().all()
            .auth().oauth2(token)
            .when()
            .put("/api/posts/{postId}/likes", postId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value());
    }

    private void followSomeUsers(String authorToken, List<User> likeUsers) {
        for (int i = 0; i < 2; i++) {
            followUser(
                authorToken,
                likeUsers
                    .get(i)
                    .getName()
            );
        }
    }

    private void followUser(String token, String targetUserName) {
        RestAssured.given().log().all()
            .auth().oauth2(token)
            .when()
            .post("/api/profiles/{targetUserName}/followings?githubFollowing=false", targetUserName)
            .then().log().all()
            .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않은 포스트를 조회하면 500예외가 발생한다. - 비 로그인/실패")
    @Test
    void searchLikeUsers_InvalidPostId_500Exception() {
        // given
        Long postId = 1L;

        // when
        PostNotFoundException response =
            requestInvalidPostLikeUsersForGuest(postId)
                .as(PostNotFoundException.class);

        // then
        assertThat(response)
            .isInstanceOf(PostNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0002")
            .hasFieldOrPropertyWithValue("message", "해당하는 게시물을 찾을 수 없습니다.");
    }

    private ExtractableResponse<Response> requestInvalidPostLikeUsersForGuest(Long postId) {
        return RestAssured.given().log().all()
            .when()
            .get("/api/posts/{postId}/likes", postId)
            .then().log().all()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .extract();
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
            name, "http://img.com", "hi~", "github.com/",
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
            .statusCode(HttpStatus.OK.value())
            .extract();
    }
}
