package com.woowacourse.pickgit.acceptance.post.like;

import static com.woowacourse.pickgit.common.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.MARK;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PostLikeCreateAcceptanceTest extends AcceptanceTest {

    @DisplayName("로그인 사용자는 게시물을 좋아요 할 수 있다. - 성공")
    @Test
    void likePost_LoginUser_Success() {
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        LikeResponse likeResponse =
            MARK.은로그인을하고().포스트에좋아요를누른다(NEOZALPOST).as(LikeResponse.class);

        assertThat(likeResponse.getLikesCount()).isOne();
        assertThat(likeResponse.getLiked()).isTrue();
    }

    @DisplayName("게스트는 게시물을 좋아요 할 수 없다. - 실패")
    @Test
    void likePost_GuestUser_401ExceptionThrown() {
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        ExtractableResponse<Response> extractableResponse = GUEST.는().포스트에좋아요를누른다(NEOZALPOST);

        int statusCode = extractableResponse.statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("로그인 사용자는 이미 좋아요한 게시물을 좋아요 할 수 없다. - 실패")
    @Test
    void likePost_DuplicatedLike_400ExceptionThrown() {
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        MARK.은로그인을하고().포스트에좋아요를누른다(NEOZALPOST);
        ExtractableResponse<Response> extractableResponse =
            MARK.은로그인을하고().포스트에좋아요를누른다(NEOZALPOST);

        int statusCode = extractableResponse.statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("P0003");
    }
}
