package com.woowacourse.pickgit.unit.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.Posts;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostsTest {

    @DisplayName("post의 수를 반환한다.")
    @Test
    void getCounts_getCountsOfPosts_returnCountsOfPosts() {
        Posts posts = new Posts(List.of(
            new Post(null, null, null, null),
            new Post(null, null, null, null),
            new Post(null, null, null, null)
        ));

        assertThat(posts.getCounts()).isEqualTo(3);
    }
}
