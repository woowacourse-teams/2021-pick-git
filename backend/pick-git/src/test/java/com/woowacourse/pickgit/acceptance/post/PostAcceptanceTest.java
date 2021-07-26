package com.woowacourse.pickgit.acceptance.post;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.post.application.dto.CommentResponse;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.request.ContentRequest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.Null;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(InfrastructureTestConfiguration.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class PostAcceptanceTest {

    private static final String ANOTHER_USERNAME = "pick-git-login";
    private static final String USERNAME = "jipark3";

    @LocalServerPort
    int port;

    @MockBean
    private OAuthClient oAuthClient;

    private String githubRepoUrl;
    private List<String> tags;
    private String content;

    private Map<String, Object> request;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        githubRepoUrl = "https://github.com/woowacourse-teams/2021-pick-git";
        tags = List.of("java", "spring");
        content = "this is content";

        Map<String, Object> body = new HashMap<>();
        body.put("githubRepoUrl", githubRepoUrl);
        body.put("tags", tags);
        body.put("content", content);
        request = body;
    }

    @DisplayName("사용자는 게시글을 등록한다.")
    @Test
    void write_LoginUser_Success() {
        // given
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();

        // when
        requestWrite(token);
    }

    @DisplayName("로그인일때 게시물을 조회한다. - Comment 및 게시글의 좋아요 여부를 확인할 수 있다.")
    @Test
    void read_LoginUser_Success() {
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();

        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);

        List<PostResponseDto> response = given().log().all()
            .auth().oauth2(token)
            .when()
            .get("/api/posts?page=0&limit=3")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<List<PostResponseDto>>() {
            });

        assertThat(response).hasSize(3);
    }

    @DisplayName("비 로그인이어도 게시글 조회가 가능하다. - Comment 및 게시물 좋아요 여부는 항상 false")
    @Test
    void read_GuestUser_Success() {
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();

        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);

        List<PostResponseDto> response = given().log().all()
            .when()
            .get("/api/posts?page=0&limit=3")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<List<PostResponseDto>>() {
            });

        assertThat(response).hasSize(3);
    }

    @DisplayName("로그인 상태에서 내 피드 조회가 가능하다.")
    @Test
    void readMyFeed_LoginUser_Success() {
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();

        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);

        List<PostResponseDto> response = given().log().all()
            .auth().oauth2(token)
            .when()
            .get("/api/posts/me?page=0&limit=3")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<List<PostResponseDto>>() {
            });

        assertThat(response).hasSize(3);
    }

    @DisplayName("비로그인 상태에서는 내 피드 조회가 불가능하다.")
    @Test
    void readMyFeed_GuestUser_Success() {
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();

        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);

        given().log().all()
            .when()
            .get("/api/posts/me?page=0&limit=3")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("로그인 상태에서 다른 유저 피드 조회가 가능하다.")
    @Test
    void readUserFeed_LoginUser_Success() {
        String loginUserToken = 로그인_되어있음(USERNAME).getToken();
        String targetUserToken = 로그인_되어있음(ANOTHER_USERNAME).getToken();

        requestToWritePostApi(targetUserToken, HttpStatus.CREATED);
        requestToWritePostApi(targetUserToken, HttpStatus.CREATED);
        requestToWritePostApi(targetUserToken, HttpStatus.CREATED);
        requestToWritePostApi(loginUserToken, HttpStatus.CREATED);
        requestToWritePostApi(loginUserToken, HttpStatus.CREATED);

        List<PostResponseDto> response = given().log().all()
            .auth().oauth2(loginUserToken)
            .when()
            .get("/api/posts/" + ANOTHER_USERNAME + "?page=0&limit=3")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<List<PostResponseDto>>() {
            });

        assertThat(response).hasSize(3);
    }

    @DisplayName("로그인 상태에서 다른 유저 피드 조회가 가능하다.")
    @Test
    void readUserFeed_GuestUser_Success() {
        String targetUserToken = 로그인_되어있음(ANOTHER_USERNAME).getToken();

        requestToWritePostApi(targetUserToken, HttpStatus.CREATED);
        requestToWritePostApi(targetUserToken, HttpStatus.CREATED);
        requestToWritePostApi(targetUserToken, HttpStatus.CREATED);

        List<PostResponseDto> response = given().log().all()
            .when()
            .get("/api/posts/" + ANOTHER_USERNAME + "?page=0&limit=3")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<List<PostResponseDto>>() {
            });

        assertThat(response).hasSize(3);
    }

    @DisplayName("게스트는 게시글을 등록할 수 없다. - 유효하지 않은 토큰이 있는 경우 (Authorization header O)")
    @Test
    void write_GuestUserWithToken_Fail() {
        // given
        String token = "Bearer guest";

        // when
        requestToWritePostApi(token, HttpStatus.UNAUTHORIZED);
    }

    @DisplayName("게스트는 게시글을 등록할 수 없다. - 토큰이 없는 경우 (Authorization header X)")
    @Test
    void write_GuestUserWithoutToken_Fail() {
        // when
        given().log().all()
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .formParams(request)
            .multiPart("images", FileFactory.getTestImage1File())
            .multiPart("images", FileFactory.getTestImage2File())
            .when()
            .post("/api/posts")
            .then().log().all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .extract();
    }

    private ExtractableResponse<Response> requestToWritePostApi(String token,
        HttpStatus httpStatus) {
        return given().log().all()
            .auth().oauth2(token)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .formParams(request)
            .multiPart("images", FileFactory.getTestImage1File())
            .multiPart("images", FileFactory.getTestImage2File())
            .when()
            .post("/api/posts")
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    @DisplayName("User는 Comment을 등록할 수 있다.")
    @Test
    void addComment_LoginUser_Success() {
        // given
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();
        Long postId = 1L;

        requestWrite(token);

        ContentRequest request = new ContentRequest("this is content");

        // when
        CommentResponse response = requestAddComment(token, postId, request, HttpStatus.OK)
            .as(CommentResponse.class);

        // then
        assertThat(response.getAuthorName()).isEqualTo(ANOTHER_USERNAME);
        assertThat(response.getContent()).isEqualTo("this is content");
    }

    @DisplayName("비로그인 User는 Comment를 등록할 수 없다.")
    @Test
    void addComment_GuestUser_Fail() {
        // given
        String writePostToken = 로그인_되어있음(ANOTHER_USERNAME).getToken();
        String invalidToken = "invalid token";
        Long postId = 1L;

        requestWrite(writePostToken);

        ContentRequest request = new ContentRequest("this is content");

        // when
        ApiErrorResponse response
            = requestAddComment(invalidToken, postId, request, HttpStatus.UNAUTHORIZED)
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    private void requestWrite(String token) {
        given().log().all()
            .auth().oauth2(token)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .formParams(request)
            .multiPart("images", FileFactory.getTestImage1File())
            .multiPart("images", FileFactory.getTestImage2File())
            .when()
            .post("/api/posts")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value())
            .extract();
    }

    @DisplayName("Comment 내용이 빈 경우 예외가 발생한다. - 400 예외")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void addComment_NullOrEmpty_400Exception(String content) {
        // given
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();
        Long postId = 1L;
        ContentRequest request = new ContentRequest(content);

        // when
        ApiErrorResponse response = requestAddComment(token, postId, request, HttpStatus.BAD_REQUEST)
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("F0001");
    }

    @DisplayName("존재하지 않는 Post에 Comment를 등록할 수 없다. - 500 예외")
    @Test
    void addComment_PostNotFound_500Exception() {
        // given
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();
        Long postId = 0L;
        ContentRequest request = new ContentRequest("a");

        // when
        ApiErrorResponse response = requestAddComment(token, postId, request, HttpStatus.INTERNAL_SERVER_ERROR)
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("P0002");
    }

    @DisplayName("Comment 내용이 100자 초과인 경우 예외가 발생한다. - 400 예외")
    @Test
    void addComment_Over100_400Exception() {
        // given
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();
        Long postId = 1L;
        ContentRequest request = new ContentRequest("a".repeat(101));

        // when
        ApiErrorResponse response = requestAddComment(token, postId, request, HttpStatus.BAD_REQUEST)
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("F0002");
    }

    private ExtractableResponse<Response> requestAddComment(
        String token,
        Long postId,
        ContentRequest request,
        HttpStatus httpStatus) {
        return given().log().all()
            .auth().oauth2(token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .post("/api/posts/{postId}/comments", postId)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    @DisplayName("사용자는 Repository 목록을 가져올 수 있다.")
    @Test
    void showRepositories_LoginUser_Success() {
        // given
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();

        // when
        List<RepositoryResponseDto> response =
            request(token, USERNAME, HttpStatus.OK.value())
                .as(new TypeRef<List<RepositoryResponseDto>>() {
                });

        // then
        assertThat(response).hasSize(2);
    }

    @DisplayName("토큰이 유효하지 않은 경우 예외가 발생한다. - 500 예외")
    @Test
    void showRepositories_InvalidAccessToken_500Exception() {
        // given
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();

        // when
        request(token + "hi", USERNAME, HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("사용자가 유효하지 않은 경우 예외가 발생한다. - 500 예외")
    @Test
    void showRepositories_InvalidUsername_400Exception() {
        // given
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();

        // when
        ApiErrorResponse response =
            request(token, USERNAME + "pika", HttpStatus.INTERNAL_SERVER_ERROR.value())
                .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("V0001");
    }

    private ExtractableResponse<Response> request(String token, String username, int statusCode) {
        return given().log().all()
            .auth().oauth2(token)
            .when()
            .get("/api/github/{username}/repositories", username)
            .then().log().all()
            .statusCode(statusCode)
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
            name, "image", "hi~", "github.com/",
            null, null, null, null
        );

        given(oAuthClient.getAccessToken(oauthCode))
            .willReturn(accessToken);
        given(oAuthClient.getGithubProfile(accessToken))
            .willReturn(oAuthProfileResponse);

        // when
        return given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/afterlogin?code=" + oauthCode)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }
}
