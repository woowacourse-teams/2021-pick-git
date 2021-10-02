package com.woowacourse.pickgit.query.acceptance.post;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class PostAcceptanceTest_Query extends AcceptanceTest {

    private static final String ANOTHER_USERNAME = "pick-git-login";
    private static final String USERNAME = "jipark3";

    private String githubRepoUrl;
    private String content;
    private Map<String, Object> request;

    @MockBean
    private OAuthClient oAuthClient;

    @BeforeEach
    void setUp() {
        githubRepoUrl = "https://github.com/woowacourse-teams/2021-pick-git";
        List<String> tags = List.of("java", "spring");
        content = "this is content";

        Map<String, Object> body = new HashMap<>();
        body.put("githubRepoUrl", githubRepoUrl);
        body.put("tags", tags);
        body.put("content", content);
        request = body;
    }

    @DisplayName("로그인일때 홈 피드를 조회한다. - 게시글 좋아요 여부 true/false")
    @Test
    void readHomeFeed_LoginUser_Success() {
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
            .as(new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(3)
            .extracting("liked")
            .containsExactly(false, false, false);
    }

    @DisplayName("비 로그인이어도 홈 피드 조회가 가능하다. - 게시물 좋아요 여부는 항상 null")
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
            .as(new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(3)
            .extracting("liked")
            .containsExactly(null, null, null);
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
            .as(new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(3)
            .extracting("liked")
            .containsExactly(false, false, false);
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

        List<PostResponseDto> response = given().log().all()
            .auth().oauth2(loginUserToken)
            .when()
            .get("/api/posts/" + ANOTHER_USERNAME + "?page=0&limit=3")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(3)
            .extracting("liked")
            .containsExactly(false, false, false);
    }

    @DisplayName("비 로그인 상태에서 다른 유저 피드 조회가 가능하다.")
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
            .as(new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(3)
            .extracting("liked")
            .containsExactly(null, null, null);
    }

    private void requestToWritePostApi(String token, HttpStatus httpStatus) {
        given().log().all()
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
