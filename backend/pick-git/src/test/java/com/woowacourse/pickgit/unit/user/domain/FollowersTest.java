package com.woowacourse.pickgit.unit.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.user.DuplicateFollowException;
import com.woowacourse.pickgit.exception.user.InvalidFollowException;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.follow.Follow;
import com.woowacourse.pickgit.user.domain.follow.Followers;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @DisplayName("add 메서드는")
    @Nested
    class Describe_add {

        @DisplayName("현재 팔로워 목록에 동일한 Follow 정보가 없을 때")
        @Nested
        class Context_NotDuplicateFollowExits {

            @DisplayName("팔로워 목록에 Follow 정보를 추가한다.")
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
        }

        @DisplayName("현재 팔로워 목록에 동일한 Follow 정보가 존재하면")
        @Nested
        class Context_DuplicateFollowExits {

            @DisplayName("팔로워 목록에 Follow 정보를 추가할 수 없다.")
            @Test
            void add_ExceptionThrown() {
                // given
                User user1 = UserFactory.user(1L, "kevin");
                User user2 = UserFactory.user(2L, "danyee");

                // when, then
                assertThatCode(() -> followers.add(new Follow(user1, user2)))
                    .isInstanceOf(DuplicateFollowException.class)
                    .hasMessage("이미 팔로우 중 입니다.");
            }
        }
    }

    @DisplayName("remove 메서드는")
    @Nested
    class Describe_remove {

        @DisplayName("팔로워 목록에 동일한 Follow 정보가 있을 때")
        @Nested
        class Context_DuplicateFollowExits {

            @DisplayName("팔로워 목록에 Follow 정보를 삭제한다.")
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

        @DisplayName("팔로워 목록에 동일한 Follow 정보가 없을 때")
        @Nested
        class Context_NotDuplicateFollowExits {

            @DisplayName("팔로워 목록에 Follow 정보를 삭제할 수 없다.")
            @Test
            void remove_ExceptionThrown() {
                // given
                User user1 = UserFactory.user(3L, "kadfevin");
                User user2 = UserFactory.user(4L, "danyafdadfee");

                // when, then
                assertThatCode(() -> followers.remove(new Follow(user1, user2)))
                    .isInstanceOf(InvalidFollowException.class)
                    .hasMessage("존재하지 않는 팔로우 입니다.");
            }
        }
    }
}
