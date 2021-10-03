package com.woowacourse.pickgit.acceptance.comment;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.comment.presentation.dto.request.ContentRequest;
import com.woowacourse.pickgit.common.factory.FileFactory;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class CommentAcceptanceTest_delete extends AcceptanceTest {

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

    @DisplayName("내 게시물, 내 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByMeAndIsCommentedByMe_Success() {
        // given
        String me = 로그인_되어있음("dani").getToken();

        requestWrite(me);
        requestAddComment(me, 1L);
        requestAddComment(me, 1L);

        // when
        requestDeleteComment(me, 1L, 1L, HttpStatus.NO_CONTENT);
    }

    @DisplayName("내 게시물, 남 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByMeAndIsCommentedByOther_Success() {
        // given
        String me = 로그인_되어있음("dani").getToken();
        String other = 로그인_되어있음("dada").getToken();

        requestWrite(me);
        requestAddComment(other, 1L);
        requestAddComment(other, 1L);

        // when
        requestDeleteComment(me, 1L, 1L, HttpStatus.NO_CONTENT);
    }

    @DisplayName("남 게시물, 내 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByOtherAndIsCommentedByMe_Success() {
        // given
        String me = 로그인_되어있음("dani").getToken();
        String other = 로그인_되어있음("dada").getToken();

        requestWrite(other);
        requestAddComment(me, 1L);
        requestAddComment(me, 1L);

        // when
        requestDeleteComment(me, 1L, 1L, HttpStatus.NO_CONTENT);
    }

    @DisplayName("남 게시물, 남 댓글은 삭제할 수 없다. - 401 예외")
    @Test
    void delete_isWrittenByOtherAndIsCommentedByOther_401Exception() {
        // given
        String me = 로그인_되어있음("dani").getToken();
        String other = 로그인_되어있음("dada").getToken();

        requestWrite(other);
        requestAddComment(other, 1L);
        requestAddComment(other, 1L);

        // when
        requestDeleteComment(me, 1L, 1L, HttpStatus.UNAUTHORIZED);
    }

    @DisplayName("존재하지 않는 댓글은 삭제할 수 없다. - 400 예외")
    @Test
    void delete_isWrittenByOtherAndIsCommentedByOther_400Exception() {
        // given
        String me = 로그인_되어있음("dani").getToken();
        String other = 로그인_되어있음("dada").getToken();

        requestWrite(me);
        requestAddComment(other, 1L);
        requestAddComment(other, 1L);

        // when
        requestDeleteComment(me, 1L, 3L, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("게스트는 댓글을 삭제할 수 없다. - 401 예외")
    @Test
    void delete_GuestUser_401Exception() {
        // given
        String me = 로그인_되어있음("dani").getToken();
        String other = 로그인_되어있음("dada").getToken();

        requestWrite(me);
        requestAddComment(other, 1L);
        requestAddComment(other, 1L);

        // when
        requestDeleteComment("invalidToken", 1L, 2L, HttpStatus.UNAUTHORIZED);
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

    private ExtractableResponse<Response> requestAddComment(String token, Long postId) {
        return given().log().all()
            .auth().oauth2(token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new ContentRequest("this is content"))
            .when()
            .post("/api/posts/{postId}/comments", postId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }

    private void requestDeleteComment(
        String token,
        Long postId,
        Long commentId,
        HttpStatus httpStatus
    ) {
        given().log().all()
            .auth().oauth2(token)
            .when()
            .delete("/api/posts/{postId}/comments/{commentId}", postId, commentId)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }
}
