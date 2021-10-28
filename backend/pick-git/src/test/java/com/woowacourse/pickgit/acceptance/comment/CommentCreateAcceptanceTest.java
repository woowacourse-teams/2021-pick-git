package com.woowacourse.pickgit.acceptance.comment;

import static com.woowacourse.pickgit.common.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.common.fixture.TPost.UNKNOWN;
import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.MARK;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.comment.presentation.dto.response.CommentResponse;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CommentCreateAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
    }

    @DisplayName("User는 Comment을 등록할 수 있다.")
    @Test
    void addComment_LoginUser_Success() {
        // given
        String commentValue = "hello neozal";
        CommentResponse comment = MARK.은로그인을하고().댓글을등록한다(NEOZALPOST, commentValue)
            .as(CommentResponse.class);

        // then
        assertThat(comment.getAuthorName()).isEqualTo(MARK.name());
        assertThat(comment.getContent()).isEqualTo(commentValue);
    }

    @DisplayName("비로그인 User는 Comment를 등록할 수 없다.")
    @Test
    void addComment_GuestUser_Fail() {
        // when
        ApiErrorResponse response = GUEST.는().댓글을등록한다(NEOZALPOST, "hello neozal")
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("Comment 내용이 빈 경우 예외가 발생한다. - 400 예외")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void addComment_NullOrEmpty_400Exception(String content) {
        // when
        ApiErrorResponse response = MARK.은로그인을하고().댓글을등록한다(NEOZALPOST, content)
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("F0001");
    }

    @DisplayName("존재하지 않는 Post에 Comment를 등록할 수 없다. - 500 예외")
    @Test
    void addComment_PostNotFound_500Exception() {
        // when
        ApiErrorResponse response = MARK.은로그인을하고().댓글을등록한다(UNKNOWN, "hello me")
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("P0002");
    }

    @DisplayName("Comment 내용이 100자 초과인 경우 예외가 발생한다. - 400 예외")
    @Test
    void addComment_Over100_400Exception() {
        // when
        String overThen100Content = "a".repeat(101);
        ApiErrorResponse response = MARK.은로그인을하고().댓글을등록한다(NEOZALPOST, overThen100Content)
            .as(ApiErrorResponse.class);

        // then
        assertThat(response.getErrorCode()).isEqualTo("F0002");
    }
}
