package com.woowacourse.pickgit.post.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.config.StorageConfiguration;
import com.woowacourse.pickgit.post.application.CommentResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.post.domain.comment.Comments;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
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
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@Import({StorageConfiguration.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class PostAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private OAuthService oAuthService;

    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        LoginUser loginUser = new LoginUser("kevin", "token");
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(loginUser);

        post = new Post(null, null, null, null, null, new Comments(), new ArrayList<>(), null);
        user =
            new User(new BasicProfile("kevin", "a.jpg", "a"),
                new GithubProfile("github.com", "a", "a", "a", "a"));
        postRepository.save(post);
        userRepository.save(user);
    }

    @DisplayName("특정 Post에 댓글을 추가한다.")
    @Test
    void addComment_ValidContent_Success() {
        String url = "/api/posts/" + post.getId() + "/comments";

        CommentResponseDto response = addCommentApi(url, "test comment", HttpStatus.OK)
            .as(CommentResponseDto.class);

        assertThat(response.getAuthorName()).isEqualTo("kevin");
        assertThat(response.getContent()).isEqualTo("test comment");
    }

    private ExtractableResponse<Response> addCommentApi(String url, String body,
        HttpStatus httpStatus)
    {
        return RestAssured.given().log().all()
            .auth().oauth2("Bearer test")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(body)
            .when().post(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    @DisplayName("특정 Post에 100자가 넘는 댓글은 등록하지 못한다.")
    @Test
    void addComment_InvalidContent_ExceptionThrown() {
        String url = "/api/posts/" + post.getId() + "/comments";

        StringBuilder comment = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            comment.append("a");
        }

        String response = addCommentApi(url, comment.toString(), HttpStatus.BAD_REQUEST)
            .asString();

        assertThat(response).isEqualTo("F0002");
    }
}
