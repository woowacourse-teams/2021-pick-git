package com.woowacourse.pickgit.unit.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.user.DuplicateFollowException;
import com.woowacourse.pickgit.exception.user.InvalidFollowException;
import com.woowacourse.pickgit.exception.user.SameSourceTargetUserException;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

    private User source;
    private User target;

    @BeforeEach
    void setUp() {
        // given
        source = UserFactory.createUser(1L, "source");
        target = UserFactory.createUser(2L, "target");
    }

    @DisplayName("User는 자신의 GithubProfile을 변경한다.")
    @Test
    void changeGithubProfile_Valid_Success() {
        // given
        GithubProfile beforeProfile =
            new GithubProfile("before-url", "company", "twitter", "abc", "kkk");
        GithubProfile afterProfile =
            new GithubProfile("after-url", "pick-git", "insta", "kkk", "js");
        User user = new User(null, null, beforeProfile, null, null, null);

        // when
        user.changeGithubProfile(afterProfile);

        //then
        assertThat(user)
            .extracting("githubProfile")
            .isSameAs(afterProfile);
    }

    @DisplayName("Follow 메서드는")
    @Nested
    class Describe_follow {

        @DisplayName("아직 팔로우하지 않은 타인 User에 대해서")
        @Nested
        class Context_ValidOtherUser {

            @DisplayName("팔로우에 성공한다.")
            @Test
            void follow_Success() {
                // when
                source.follow(target);

                // then
                assertThat(source.getFollowingCount()).isEqualTo(1);
                assertThat(target.getFollowerCount()).isEqualTo(1);
            }
        }

        @DisplayName("이미 팔로우하고 있는 타인 User에 대해서")
        @Nested
        class Context_AlreadyFollowingOtherUser {

            @DisplayName("팔로우 할 수 없다.")
            @Test
            void follow_DuplicateUser_Failure() {
                // given
                source.follow(target);

                // when, then
                assertThatCode(() -> source.follow(target))
                    .isInstanceOf(DuplicateFollowException.class)
                    .hasMessage("이미 팔로우 중 입니다.");
            }
        }

        @DisplayName("자기 자신에 대해서")
        @Nested
        class Context_Myself {

            @DisplayName("팔로우할 수 없다.")
            @Test
            void follow_Myself_Failure() {
                // when, then
                assertThatCode(() -> source.follow(source))
                    .isInstanceOf(SameSourceTargetUserException.class)
                    .hasMessage("같은 Source 와 Target 유저입니다.");
            }
        }
    }

    @DisplayName("Unfollow 메서드는")
    @Nested
    class Describe_unfollow {

        @DisplayName("팔로우하고 있는 타인 User에 대해서")
        @Nested
        class Context_ValidOtherUser {

            @DisplayName("언팔로우에 성공한다.")
            @Test
            void unfollow_Success() {
                // given
                source.follow(target);

                // when
                source.unfollow(target);

                // then
                assertThat(source.getFollowingCount()).isZero();
                assertThat(target.getFollowerCount()).isZero();
            }
        }

        @DisplayName("팔로우하고 있지 않는 타인 User에 대해서")
        @Nested
        class Context_NotFollowingOtherUser {

            @DisplayName("언팔로우에 실패한다.")
            @Test
            void unfollow_NotFollowingUser_Failure() {
                // when, then
                assertThatCode(() -> source.unfollow(target))
                    .isInstanceOf(InvalidFollowException.class)
                    .hasMessage("존재하지 않는 팔로우 입니다.");
            }
        }

        @DisplayName("자기 자신에 대해서")
        @Nested
        class Context_Myself {

            @DisplayName("언팔로우할 수 없다.")
            @Test
            void unfollow_Myself_Failure() {
                // when, then
                assertThatCode(() -> source.unfollow(source))
                    .isInstanceOf(SameSourceTargetUserException.class)
                    .hasMessage("같은 Source 와 Target 유저입니다.");
            }
        }
    }

    @DisplayName("isFollowing 메서드는")
    @Nested
    class Describe_isFollowing {

        @DisplayName("현재 팔로우 중이면 True를 반환한다.")
        @Test
        void isFollowing_Valid_True() {
            // given
            source.follow(target);

            // when, then
            assertThat(source.isFollowing(target)).isTrue();
        }

        @DisplayName("현재 팔로우 중이 아니라면 False를 반환한다.")
        @Test
        void isFollowing_Invalid_False() {
            // when, then
            assertThat(source.isFollowing(target)).isFalse();
        }
    }

    @DisplayName("equals 메서드는")
    @Nested
    class Describe_equals {

        @DisplayName("ID 식별자가 동일하면 동일 엔티티로 인식한다.")
        @Test
        void equals_SameId_True() {
            User source = UserFactory.createUser(1L, "kevin");
            User target = UserFactory.createUser(1L, "mark");

            assertThat(source).isEqualTo(target);
        }

        @DisplayName("ID 식별자가 다르면 다른 엔티티로 인식한다.")
        @Test
        void equals_DifferentId_False() {
            assertThat(source).isNotEqualTo(target);
        }
    }
}
