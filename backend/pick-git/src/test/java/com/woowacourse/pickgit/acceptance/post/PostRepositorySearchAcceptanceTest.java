package com.woowacourse.pickgit.acceptance.post;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponseDto;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class PostRepositorySearchAcceptanceTest extends AcceptanceTest {

    private static final String USERNAME = "jipark3";

    @MockBean
    private OAuthClient oAuthClient;

    @DisplayName("사용자는 Repository 목록을 키워드 검색으로 가져올 수 있다.")
    @Test
    void searchUserRepositories_LoginUser_Success() {
        // given
        String token = 로그인_되어있음(USERNAME).getToken();
        String keyword = "woowa";
        int page = 0;
        int limit = 2;

        // when
        List<RepositoryResponseDto> response =
            given().log().all()
                .auth().oauth2(token)
                .when()
                .get(
                    "/api/github/search/repositories?keyword={keyword}&page={page}&limit={limit}",
                    keyword, page, limit
                )
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {});

        // then
        assertThat(response).hasSize(2);
    }

    @DisplayName("레포지토리 검색 시 토큰이 없는 경우 예외가 발생한다. - 401 예외")
    @Test
    void searchUserRepositories_InvalidAccessToken_401Exception() {
        // given
        String keyword = "woowa";
        int page = 0;
        int limit = 2;

        // when then
        InvalidTokenException exception =
            given().log().all()
                .when()
                .get(
                    "/api/github/search/repositories?keyword={keyword}&page={page}&limit={limit}",
                    keyword, page, limit
                )
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract()
                .as(new TypeRef<>() {});

        assertThat(exception.getErrorCode()).isEqualTo("A0001");
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
