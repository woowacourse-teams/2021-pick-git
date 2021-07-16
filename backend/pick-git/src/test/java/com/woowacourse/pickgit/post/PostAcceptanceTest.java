package com.woowacourse.pickgit.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.common.FileFactory;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.post.application.dto.PostDto;
import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(PostTestConfiguration.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class PostAcceptanceTest {

    private static final String ANOTHER_USERNAME = "pick-git-login";
    private static final String USERNAME = "jipark3";

    @LocalServerPort
    int port;

    @MockBean
    private OAuthClient oAuthClient;

    private List<MultipartFile> images;
    private String githubRepoUrl;
    private List<String> tags;
    private String content;

    private Map<String, Object> request;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        images = List.of(
            FileFactory.getTestImage1(),
            FileFactory.getTestImage2()
        );
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
        RestAssured
            .given().log().all()
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

    @DisplayName("로그인일때 게시물을 조회한다. - 댓글 및 게시글의 좋아요 여부를 확인할 수 있다.")
    @Test
    void read_LoginUser_Success() {
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();

        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);

        List<PostDto> response = RestAssured
            .given().log().all()
            .auth().oauth2(token)
            .when()
            .get("/api/posts?page=0&limit=3")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<List<PostDto>>() {
            });

        assertThat(response).hasSize(3);
    }

    @DisplayName("비 로그인이어도 게시글 조회가 가능하다. - 댓글 및 게시물 좋아요 여부는 항상 false")
    @Test
    void read_GuestUser_Success() {
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();

        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);

        List<PostDto> response = RestAssured
            .given().log().all()
            .when()
            .get("/api/posts?page=0&limit=3")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<List<PostDto>>() {
            });

        assertThat(response).hasSize(3);
    }

    private ExtractableResponse<Response> requestToWritePostApi(String token, HttpStatus httpStatus) {
        return RestAssured
            .given().log().all()
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

    @DisplayName("로그인 상태에서 내 피드 조회가 가능하다.")
    @Test
    void readMyFeed_LoginUser_Success() {
        String token = 로그인_되어있음(ANOTHER_USERNAME).getToken();

        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);

        List<PostDto> response = RestAssured
            .given().log().all()
            .auth().oauth2(token)
            .when()
            .get("/api/posts/me?page=0&limit=3")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<List<PostDto>>() {
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

         RestAssured
            .given().log().all()
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

        List<PostDto> response = RestAssured
            .given().log().all()
            .auth().oauth2(loginUserToken)
            .when()
            .get("/api/posts/" + ANOTHER_USERNAME +"?page=0&limit=3")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<List<PostDto>>() {
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

        List<PostDto> response = RestAssured
            .given().log().all()
            .when()
            .get("/api/posts/" + ANOTHER_USERNAME +"?page=0&limit=3")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<List<PostDto>>() {
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
        RestAssured
            .given().log().all()
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
        return RestAssured
            .given().log().all()
            .auth().oauth2(token)
            .when()
            .get("/api/github/{username}/repositories", username)
            .then().log().all()
            .statusCode(statusCode)
            .extract();
    }



    private OAuthTokenResponse 로그인_되어있음(String name) {
        OAuthTokenResponse response = 로그인_요청(name).as(OAuthTokenResponse.class);
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
