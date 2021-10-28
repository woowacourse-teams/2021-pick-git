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

class PostLikeDeleteAcceptanceTest extends AcceptanceTest {

    @DisplayName("로그인 사용자는 게시물을 좋아요 취소 할 수 있다. - 성공")
    @Test
    void unlikePost_LoginUser_Success() {
        toWrite();
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        MARK.은로그인을하고().포스트에좋아요를누른다(NEOZALPOST);
        LikeResponse response =
            MARK.은로그인을하고().포스트에좋아요_취소를_한다(NEOZALPOST).as(LikeResponse.class);

        assertThat(response.getLikesCount()).isZero();
        assertThat(response.getLiked()).isFalse();
    }


    @DisplayName("게스트는 게시물을 좋아요 취소 할 수 없다. - 실패")
    @Test
    void unlikePost_GuestUser_401ExceptionThrown() {
        toWrite();
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        ExtractableResponse<Response> extractableResponse = GUEST.는().포스트에좋아요_취소를_한다(NEOZALPOST);

        int statusCode = extractableResponse.statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("로그인 사용자는 좋아요 하지 않은 게시물을 좋아요 취소 할 수 없다. - 실패")
    @Test
    void unlikePost_cannotUnlike_400ExceptionThrown() {
        toWrite();
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        ExtractableResponse<Response> extractableResponse = MARK.은로그인을하고().포스트에좋아요_취소를_한다(NEOZALPOST);

        int statusCode = extractableResponse.statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("P0004");
    }
}
