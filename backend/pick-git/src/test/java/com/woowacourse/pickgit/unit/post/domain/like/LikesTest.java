package com.woowacourse.pickgit.unit.post.domain.like;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.like.Like;
import com.woowacourse.pickgit.post.domain.like.Likes;
import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LikesTest {

    private Likes likes;
    private Post post;

    @BeforeEach
    void setUp() {
        post = Post.builder().id(1L).build();

        User testUser1 = UserFactory.user(1L, "testUser1");
        User testUser2 = UserFactory.user(2L, "testUser2");
        User testUser3 = UserFactory.user(3L, "testUser3");

        likes = new Likes(List.of(
            new Like(post, testUser1),
            new Like(post, testUser2),
            new Like(post, testUser3)
        ));
    }

    @DisplayName("like의 개수를 확인한다.")
    @Test
    void getCounts() {
        assertThat(likes.getCounts()).isEqualTo(3);
    }

    @DisplayName("특정 사용자의 like가 포함되어 있는지 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"1, testUser1, true", "2,testUser2, true", "4, noTestUser1, false"})
    void contains(Long id, String userName, boolean expected) {
        User user = UserFactory.user(id, userName);
        assertThat(likes.contains(new Like(post, user))).isEqualTo(expected);
    }
}
