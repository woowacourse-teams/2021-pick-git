package com.woowacourse.pickgit.acceptance.post;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.config.count_data_source.QueryCounter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class PostCacheAcceptanceTest extends AcceptanceTest {

    @Autowired
    private QueryCounter queryCounter;

    @MockBean
    private OAuthClient oAuthClient;

    private String githubRepoUrl;
    private String content;
    private Map<String, Object> request;

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

    @DisplayName("")
    @Test
    void readCache_GuestUser_success() {
        String token = 로그인_되어있음("guest").getToken();

        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);
        requestToWritePostApi(token, HttpStatus.CREATED);

        queryCounter.startCount();
        List<PostResponseDto> response = given().log().all()
            .when()
            .get("/api/posts?page=0&limit=3")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(new TypeRef<>() {
            });
        queryCounter.endCount();

        assertThat(response)
            .hasSize(3)
            .extracting("liked")
            .containsExactly(null, null, null);
        System.out.println("#### : " + queryCounter.getCount().getValue());
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
}
