package com.woowacourse.pickgit.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.woowacourse.pickgit.tag.domain.Tag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostTest {

    @DisplayName("Tag를 정상적으로 Post에 등록한다.")
    @Test
    void addTags_ValidTags_RegistrationSuccess() {
        Post post =
            new Post(null, null, new PostContent(), null, null, new ArrayList<>(), null);
        List<Tag> tags =
            Arrays.asList(new Tag("tag1"), new Tag("tag2"), new Tag("tag3"));

        post.addTags(tags);

        assertThat(post.getTags()).hasSize(3);
    }

    @DisplayName("중복되는 이름의 Tag가 존재하면 Post에 추가할 수 없다.")
    @Test
    void addTags_DuplicatedTagName_ExceptionThrown() {
        Post post =
            new Post(null, null, new PostContent(), null, null, new ArrayList<>(), null);
        List<Tag> tags =
            Arrays.asList(new Tag("tag1"), new Tag("tag2"), new Tag("tag3"));
        post.addTags(tags);

        List<Tag> duplicatedTags = Arrays.asList(new Tag("tag4"), new Tag("tag3"));

        assertThatCode(() -> post.addTags(duplicatedTags))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("중복되는 태그를 추가할 수 없습니다.");
    }
}
