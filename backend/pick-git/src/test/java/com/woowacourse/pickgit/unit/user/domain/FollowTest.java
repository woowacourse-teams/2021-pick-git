package com.woowacourse.pickgit.unit.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.user.SameSourceTargetUserException;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.follow.Follow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FollowTest {

    @DisplayName("Follow 인스턴스는 Source와 Target이 동일하면 생성 예외가 발생한다.")
    @Test
    void new_SameSourceTarget_Exception() {
        User source = UserFactory.user(1L, "testUser");

        assertThatCode(() -> new Follow(source, source))
            .isInstanceOf(SameSourceTargetUserException.class)
            .hasMessage("같은 Source 와 Target 유저입니다.");
    }

    @DisplayName("팔로잉을 하고 있다. - target == targetUser")
    @Test
    void isFollowing_Following_True() {
        // given
        User source = UserFactory.user(1L, "testUser");
        User target = UserFactory.user(2L, "testUser2");
        User targetUser = UserFactory.user(2L, "testUser2");

        Follow follow = new Follow(source, target);

        // then
        assertThat(follow.isFollowing(targetUser)).isTrue();
    }

    @DisplayName("팔로잉을 하고 있지 않다. - target != targetUser")
    @Test
    void isFollowing_Following_False() {
        // given
        User source = UserFactory.user(1L, "testUser");
        User target = UserFactory.user(2L, "testUser2");
        User targetUser = UserFactory.user(3L, "testUser3");

        Follow follow = new Follow(source, target);

        // then
        assertThat(follow.isFollowing(targetUser)).isFalse();
    }
}
