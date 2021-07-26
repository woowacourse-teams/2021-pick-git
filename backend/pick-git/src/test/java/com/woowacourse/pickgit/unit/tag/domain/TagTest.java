package com.woowacourse.pickgit.unit.tag.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.woowacourse.pickgit.exception.post.TagFormatException;
import com.woowacourse.pickgit.tag.domain.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class TagTest {

    @DisplayName("태그를 정상 생성한다.")
    @Test
    void newTag_ValidName_Success() {
        assertThatCode(() -> new Tag("abc"))
            .doesNotThrowAnyException();
    }

    @DisplayName("태그 이름이 null이거나 빈 문자열이거나 20자를 넘어가면 예외가 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abcdeabcdeabcdeabcdea", " ", "  "})
    void newTag_InvalidName_Failure(String name) {
        assertThatCode(() -> new Tag(name))
            .isInstanceOf(TagFormatException.class)
            .extracting("errorCode")
            .isEqualTo("F0003");
    }

    @DisplayName("태그 이름은 소문자로 자동 변환된다.")
    @Test
    void newTag_ToLowerCase_Success() {
        // given
        Tag tag = new Tag("Java");

        // when, then
        assertThat(tag.getName()).isEqualTo("java");
    }
}
