package com.woowacourse.pickgit.unit.comment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.post.CommentFormatException;
import com.woowacourse.pickgit.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CommentTest {

    @DisplayName("100자 이하의 댓글을 생성할 수 있다.")
    @Test
    void newComment_Under100Length_Success() {
        // given
        String content = "a".repeat(99);

        // when, then
        assertThatCode(() -> new Comment(content, null, null))
            .doesNotThrowAnyException();
    }

    @DisplayName("100자 초과의 댓글을 생성할 수 없다.")
    @Test
    void newComment_Over100Length_ExceptionThrown() {
        // given
        String content = "a".repeat(100);

        // when, then
        assertThatCode(() -> new Comment(content, null, null))
            .isInstanceOf(CommentFormatException.class)
            .extracting("errorCode")
            .isEqualTo("F0002");
    }

    @DisplayName("댓글은 null이거나 빈 문자열(공백만 있는 문자열 포함)이면 생성할 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void newComment_NullOrEmpty_ExceptionThrown(String content) {
        // when, then
        assertThatCode(() -> new Comment(content, null, null))
            .isInstanceOf(CommentFormatException.class)
            .extracting("errorCode")
            .isEqualTo("F0002");
    }

    @DisplayName("자신이 작성한 댓글인 아닌지 확인한다.")
    @Test
    void isNotCommentedBy_isNotCommentedByMe_Success() {
        // given
        User me = UserFactory.user(2L, "neozal");
        Comment comment = new Comment(1L, "comment1", me, null);

        assertThat(comment.isNotCommentedBy(me)).isFalse();
    }

    @DisplayName("자신이 작성한 댓글인지 확인한다.")
    @Test
    void delete_isCommentedByMe_Success() {
        // given
        User me = UserFactory.user(1L, "dani");
        Comment comment = new Comment(1L, "comment1", me, null);

        assertThat(comment.isNotCommentedBy(me)).isFalse();
    }


}
