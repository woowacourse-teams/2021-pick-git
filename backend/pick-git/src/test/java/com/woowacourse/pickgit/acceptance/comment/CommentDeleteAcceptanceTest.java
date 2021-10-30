package com.woowacourse.pickgit.acceptance.comment;

import static com.woowacourse.pickgit.common.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.KEVIN;
import static com.woowacourse.pickgit.common.fixture.TUser.MARK;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.comment.presentation.dto.response.CommentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CommentDeleteAcceptanceTest extends AcceptanceTest {

    @DisplayName("내 게시물의 내 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByMeAndIsCommentedByMe_Success() {
        // given
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        CommentResponse response = NEOZAL.은로그인을하고().댓글을등록한다(NEOZALPOST, "hello1")
            .as(CommentResponse.class);

        NEOZAL.은로그인을하고().댓글을삭제한다(NEOZALPOST, response.getId());
    }

    @DisplayName("내 게시물의 남 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByMeAndIsCommentedByOther_Success() {
        // given
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        CommentResponse response = MARK.은로그인을하고().댓글을등록한다(NEOZALPOST, "hello")
            .as(CommentResponse.class);

        NEOZAL.은로그인을하고().댓글을삭제한다(NEOZALPOST, response.getId());
    }

    @DisplayName("남 게시물의 내 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByOtherAndIsCommentedByMe_Success() {
        // given
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        CommentResponse response = MARK.은로그인을하고().댓글을등록한다(NEOZALPOST, "hello")
            .as(CommentResponse.class);

        MARK.은로그인을하고().댓글을삭제한다(NEOZALPOST, response.getId());
    }

    @DisplayName("남 게시물, 남 댓글은 삭제할 수 없다. - 401 예외")
    @Test
    void delete_isWrittenByOtherAndIsCommentedByOther_401Exception() {
        // given
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);

        // when
        CommentResponse response = MARK.은로그인을하고().댓글을등록한다(NEOZALPOST, "hello")
            .as(CommentResponse.class);
        int statusCode = KEVIN.은로그인을하고().댓글을삭제한다(NEOZALPOST, response.getId()).statusCode();

        //then
        assertThat(statusCode).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("존재하지 않는 댓글은 삭제할 수 없다. - 400 예외")
    @Test
    void delete_isWrittenByOtherAndIsCommentedByOther_400Exception() {
        // when
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        int statusCode = MARK.은로그인을하고().댓글을삭제한다(NEOZALPOST, Long.MAX_VALUE).statusCode();

        // then
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("게스트는 댓글을 삭제할 수 없다. - 401 예외")
    @Test
    void delete_GuestUser_401Exception() {
        // given
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        CommentResponse response = NEOZAL.은로그인을하고().댓글을등록한다(NEOZALPOST, "hello")
            .as(CommentResponse.class);
        int statusCode = GUEST.는().댓글을삭제한다(NEOZALPOST, response.getId()).statusCode();

        assertThat(statusCode).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
