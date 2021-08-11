package com.woowacourse.pickgit.acceptance.comment;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.presentation.dto.request.ContentRequest;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.BDDMockito;
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
public class CommentAcceptanceTest {

    private static final String ANOTHER_USERNAME = "pick-git-login";
    private static final String USERNAME = "jipark3";

    private String githubRepoUrl;
    private String content;
    private Map<String, Object> request;

    @MockBean
    private OAuthClient oAuthClient;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        githubRepoUrl = "https://github.com/woowacourse-teams/2021-pick-git";
        List<String> tags = List.of("java", "spring");
        content = "this is content";

        Map<String, Object> body = new HashMap<>();
        body.put("githubRepoUrl", githubRepoUrl);
        body.put("tags", tags);
        body.put("content", content);
        request = body;
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
        CommentResponseDto response = requestAddComment(token, postId, request, HttpStatus.OK)
            .as(CommentResponseDto.class);

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
        ApiErrorResponse response = requestAddComment(token, postId, request,
            HttpStatus.BAD_REQUEST)
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
        ApiErrorResponse response = requestAddComment(token, postId, request,
            HttpStatus.INTERNAL_SERVER_ERROR)
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
        ApiErrorResponse response = requestAddComment(token, postId, request,
            HttpStatus.BAD_REQUEST)
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("F0002");
    }

    private ExtractableResponse<Response> requestAddComment(
        String token,
        Long postId,
        ContentRequest request,
        HttpStatus httpStatus
    ) {
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

        BDDMockito.given(oAuthClient.getAccessToken(oauthCode))
            .willReturn(accessToken);
        BDDMockito.given(oAuthClient.getGithubProfile(accessToken))
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
