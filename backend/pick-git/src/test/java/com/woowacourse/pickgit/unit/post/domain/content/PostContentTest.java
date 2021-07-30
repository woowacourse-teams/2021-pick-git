package com.woowacourse.pickgit.unit.post.domain.content;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.exception.post.PostFormatException;
import com.woowacourse.pickgit.post.domain.content.PostContent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostContentTest {

    @DisplayName("게시물 내용이 500자 초과인 경우 예외가 발생한다.")
    @Test
    void validate_IsOver500_ThrowsException() {
        // given
        String content = "hi".repeat(500);

        // then
        assertThatThrownBy(() -> new PostContent(content))
            .isInstanceOf(PostFormatException.class)
            .extracting("errorCode")
            .isEqualTo("F0004");
    }
}
