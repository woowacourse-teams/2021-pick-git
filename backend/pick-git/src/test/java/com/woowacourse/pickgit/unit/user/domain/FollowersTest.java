package com.woowacourse.pickgit.unit.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.follow.Follow;
import com.woowacourse.pickgit.user.domain.follow.Followers;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FollowersTest {

    private Followers followers;

    @BeforeEach
    void setUp() {
        // given
        followers = new Followers(new ArrayList<>());
        User user1 = UserFactory.user(1L, "kevin");
        User user2 = UserFactory.user(2L, "danyee");
        followers.add(new Follow(user1, user2));
    }

    @DisplayName("팔로워 목록에 Follow를 추가한다.")
    @Test
    void add_Success() {
        // given
        User user1 = UserFactory.user(3L, "kafd2");
        User user2 = UserFactory.user(4L, "daadfadfe");

        // when
        followers.add(new Follow(user1, user2));

        // then
        assertThat(followers.count()).isEqualTo(2);
    }

    @DisplayName("팔로워 목록에 Follow를 삭제한다.")
    @Test
    void remove_Success() {
        // given
        User user1 = UserFactory.user(1L, "kevin");
        User user2 = UserFactory.user(2L, "danyee");

        // when
        followers.remove(new Follow(user1, user2));

        // then
        assertThat(followers.count()).isZero();
    }
}
