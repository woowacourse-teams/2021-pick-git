package com.woowacourse.pickgit.tag.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.authentication.application.JwtTokenProvider;
import com.woowacourse.pickgit.authentication.dao.OAuthAccessTokenDao;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
class TagIntegrationTest {

    @LocalServerPort
    private int port;

    @Value("${github.tester.user-name}")
    private String userName;

    @Value("${github.tester.access-token}")
    private String githubAccessToken;

    @Value("${github.tester.repository-name-for-tag-test}")
    private String repositoryName;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuthAccessTokenDao oAuthAccessTokenDao;

    private String userAccessToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        BasicProfile basicProfile =
            new BasicProfile(userName, "ala", "hi");
        GithubProfile githubProfile =
            new GithubProfile("a", "b", "c", "d", "e");
        User user = new User(basicProfile, githubProfile);
        userRepository.save(user);
        userAccessToken = jwtTokenProvider.createToken(userName);
        oAuthAccessTokenDao.insert(userAccessToken, githubAccessToken);
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

        TagsDto response = requestTags(userAccessToken, url, HttpStatus.OK)
            .as(TagsDto.class);

        assertThat(response.getTags()).containsExactly("JavaScript", "HTML", "CSS");
    }

    @DisplayName("유효하지 않은 레포지토리 태그 추출 요청시 404 예외 메시지가 반환된다.")
    @Test
    void extractLanguageTags_InvalidRepository_ExceptionThrown() {
        String url =
            "/api/github/" + userName + "/repositories/none-available-repo/tags/languages";

        String response = requestTags(userAccessToken, url, HttpStatus.NOT_FOUND)
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
