package com.woowacourse.pickgit.post.domain.comment;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CommentContentTest {
    private CommentContent commentContent;

    @ParameterizedTest
    @CsvSource(value = {"1", "50", "100"})
    void constructor_contentLengthLessThen100_success(int length) {
        final String content  = "a".repeat(length);

        assertThatCode(() -> new CommentContent(content))
            .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource(value = {"0", "101"})
    void constructor_invalidContentLength_ExceptionOccur(int length) {
        final String content  = "a".repeat(length);

        assertThatThrownBy(() -> new CommentContent(content))
            .isInstanceOf(RuntimeException.class);
    }
}