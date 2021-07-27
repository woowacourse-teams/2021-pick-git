package com.woowacourse.pickgit.unit.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.follow.Follow;
import com.woowacourse.pickgit.user.domain.follow.Followings;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FollowingsTest {

    private Followings followings;

    @BeforeEach
    void setUp() {
        // given
        followings = new Followings(new ArrayList<>());
        User user1 = UserFactory.user(1L, "kevin");
        User user2 = UserFactory.user(2L, "danyee");
        followings.add(new Follow(user1, user2));
    }

    @DisplayName("팔로잉 목록에 Follow를 추가한다.")
    @Test
    void add_Success() {
        // given
        User user1 = UserFactory.user(3L, "kafd2");
        User user2 = UserFactory.user(4L, "daadfadfe");

        // when
        followings.add(new Follow(user1, user2));

        // then
        assertThat(followings.count()).isEqualTo(2);
    }

    @DisplayName("팔로잉 목록에 Follow를 삭제한다.")
    @Test
    void remove_Success() {
        // given
        User user1 = UserFactory.user(1L, "kevin");
        User user2 = UserFactory.user(2L, "danyee");

        // when
        followings.remove(new Follow(user1, user2));

        // then
        assertThat(followings.count()).isZero();
    }

    @DisplayName("팔로잉 목록에서 특정 Follow 정보가 포함되어 있으면 True를 반환한다.")
    @Test
    void contains_Exist_True() {
        // given
        Follow follow = new Follow(UserFactory.user(1L, "kevin"), UserFactory.user(2L, "danyee"));

        // when, then
        assertThat(followings.contains(follow)).isTrue();
    }

    @DisplayName("팔로잉 목록에 특정 Follow 정보가 포함되어 있지 않으면 False를 반환한다.")
    @Test
    void contains_NotExists_False() {
        // given
        Follow follow = new Follow(UserFactory.user(3L, "kevin"), UserFactory.user(4L, "danyee"));

        // when, then
        assertThat(followings.contains(follow)).isFalse();
    }

    @DisplayName("특정 User가 팔로잉 목록에 존재한다면 True를 반환한다.")
    @Test
    void isFollowing_Exists_True() {
        // given
        User user = UserFactory.createUser(2L, "danyee");

        // when, then
        assertThat(followings.isFollowing(user)).isTrue();
    }

    @DisplayName("특정 User가 팔로잉 목록에 존재하지 않다면 False를 반환한다.")
    @Test
    void isFollowing_NotExists_False() {
        // given
        User user = UserFactory.createUser(3L, "coda");

        // when, then
        assertThat(followings.isFollowing(user)).isFalse();
    }
}
