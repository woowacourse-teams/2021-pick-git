package com.woowacourse.pickgit.unit.post.domain.like;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.like.Like;
import com.woowacourse.pickgit.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LikeTest {

    @DisplayName("like의 소유자를 판단한다.")
    @Test
    void isOwnedBy() {
        //given
        final String userName = "testUser1";

        Post post = new Post(1L, null, null, null, null, null, null, null);
        User user = UserFactory.user("testUser1");
        Like like = new Like(post, user);

        //when
        boolean ownedBy = like.isOwnedBy(userName);

        //then
        assertThat(ownedBy).isTrue();
    }
}