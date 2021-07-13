package com.woowacourse.pickgit.tag.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.tag.TestTagConfiguration;
import com.woowacourse.pickgit.tag.application.TagsDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestTagConfiguration.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class TagAcceptanceTest {

    @LocalServerPort
    private int port;

    @MockBean
    private OAuthService oAuthService;

    @Autowired
    private UserRepository userRepository;

    private String accessToken = "Bearer testae";
    private String userName = "jipark3";
    private String repositoryName = "doms-react";

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        LoginUser loginUser = new LoginUser(userName, "valid-token-aaaa");
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(loginUser);
        User user =
            new User(new BasicProfile(userName, "a.jpg", "a"),
                new GithubProfile("github.com", "a", "a", "a", "a"));
        userRepository.save(user);
    }

    private ExtractableResponse<Response> requestTags(String accessToken, String url,
        HttpStatus httpStatus) {
        return RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .when().get(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    @DisplayName("특정 User의 Repository에 기술된 언어 태그들을 추출한다.")
    @Test
    void extractLanguageTags_ValidRepository_ExtractionSuccess() {
        String url =
            "/api/github/" + userName + "/repositories/" + repositoryName + "/tags/languages";

        TagsDto response = requestTags(accessToken, url, HttpStatus.OK)
            .as(TagsDto.class);

        assertThat(response.getTags()).containsExactly("JavaScript", "HTML", "CSS");
    }

    @DisplayName("유효하지 않은 레포지토리 태그 추출 요청시 404 예외 메시지가 반환된다.")
    @Test
    void extractLanguageTags_InvalidRepository_ExceptionThrown() {
        String url =
            "/api/github/" + userName + "/repositories/none-available-repo/tags/languages";

        String response = requestTags(accessToken, url, HttpStatus.NOT_FOUND)
            .asString();

        assertThat(response).isEqualTo("외부 플랫폼 연동 요청 처리에 실패했습니다.");
    }

    /*
    Interceptor 예외 핸들링이 구현되면 테스트도 변경되어야 합니다.
    */
//    @DisplayName("유효하지 않은 AccessToken으로 태그 추출 요청시 401 예외 메시지가 반환된다.")
//    @Test
//    void extractLanguageTags_InvalidAccessToken_ExceptionThrown() {
//        String url =
//            "/api/github/" + userName + "/repositories/" + repositoryName + "/tags/languages";
//
//        String response = requestTags("invalidtoken", url, HttpStatus.UNAUTHORIZED)
//            .asString();
//
//        assertThat(response).isEqualTo("외부 플랫폼 연동 요청 처리에 실패했습니다.");
//    }
}
