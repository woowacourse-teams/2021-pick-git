package com.woowacourse.pickgit.unit.post.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.factory.PostBuilder;
import com.woowacourse.pickgit.post.domain.Posts;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostsTest {

    @DisplayName("post의 수를 반환한다.")
    @Test
    void getCounts_getCountsOfPosts_returnCountsOfPosts() {
        Posts posts = new Posts(List.of(
            new PostBuilder().build(),
            new PostBuilder().build(),
            new PostBuilder().build()
        ));

        assertThat(posts.getCounts()).isEqualTo(3);
    }
}
