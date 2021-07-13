package com.woowacourse.pickgit.post.domain.comment;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class CommentTest {

    @DisplayName("100자 이하의 댓글을 생성할 수 있다.")
    @Test
    void newComment_Under100Length_Success() {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            content.append("a");
        }

        assertThatCode(() -> new Comment(content.toString()))
            .doesNotThrowAnyException();
    }

    @DisplayName("100자 초과의 댓글을 생성할 수 없다.")
    @Test
    void newComment_Over100Length_ExceptionThrown() {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            content.append("a");
        }

        assertThatCode(() -> new Comment(content.toString()))
            .isInstanceOf(CommentFormatException.class);
    }

    @DisplayName("댓글은 null이거나 빈 문자열이어서는 안 된다.")
    @ParameterizedTest
    @NullAndEmptySource
    void newComment_NullOrEmpty_ExceptionThrown(String content) {
        assertThatCode(() -> new Comment(content))
            .isInstanceOf(CommentFormatException.class);
    }
}
