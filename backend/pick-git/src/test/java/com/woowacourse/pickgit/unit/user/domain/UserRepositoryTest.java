package com.woowacourse.pickgit.unit.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.JpaTestConfiguration;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Import(JpaTestConfiguration.class)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private List<User> users;

    @BeforeEach
    void setUp() {
        // given
        users = UserFactory.mockSearchUsersWithId();
        users.forEach(user -> userRepository.save(user));
        userRepository.save(UserFactory.user());

        testEntityManager.flush();
        testEntityManager.clear();
    }

    @DisplayName("유저 이름으로 유저를 조회한다.")
    @Test
    void findUserByBasicProfile_Name_ValidUserName_Success() {
        // when
        User user = userRepository
            .findByBasicProfile_Name(users.get(0).getName())
            .orElseThrow(InvalidUserException::new);

        // then
        assertThat(user)
            .usingRecursiveComparison()
            .ignoringFields("id", "portfolios")
            .isEqualTo(users.get(0));
    }

    @DisplayName("등록되지 않은 유저 이름으로 유저를 조회할 수 없다.- 400 예외")
    @Test
    void findUserByBasicProfile_Name_InvalidUserName_400Exception() {
        // when, then
        assertThat(userRepository.findByBasicProfile_Name("invalidUser")).isEmpty();
    }

    @DisplayName("저장된 유저중 유사한 이름을 가진 유저를 검색할 수 있다.")
    @Test
    void findAllByUsername_SearchUserByUsername_Success() {
        // given
        String searchKeyword = "bing";

        // when
        Pageable pageable = PageRequest.of(0, 3);
        List<User> searchResult = userRepository.searchByUsernameLike(searchKeyword, pageable);

        // then
        assertThat(searchResult).hasSize(3);
        assertThat(searchResult)
            .extracting("name")
            .containsExactly(
                users.get(0).getName(),
                users.get(1).getName(),
                users.get(2).getName()
            );
    }

    @DisplayName("저장된 유저중 유사한 이름을 가진 4, 5번째 유저를 검색할 수 있다.")
    @Test
    void findAllByUsername_SearchThirdAndFourthUserByUsername_Success() {
        // given
        String seachKeyword = "bing";

        // when
        Pageable pageable = PageRequest.of(1, 3);
        List<User> searchResult = userRepository.searchByUsernameLike(seachKeyword, pageable);

        // then
        assertThat(searchResult).hasSize(2);
        assertThat(searchResult)
            .extracting("name")
            .containsExactly(
                users.get(3).getName(),
                users.get(4).getName()
            );
    }

    @DisplayName("저장된 유저중 검색 키워드와 유사한 유저가 없으면 빈 값을 반환한다.")
    @Test
    void findAllByUsername_SearchNoMatchedUser_Success() {
        // given
        String searchKeyword = "woowatech";

        // when
        Pageable pageable = PageRequest.of(0, 3);
        List<User> searchResult = userRepository.searchByUsernameLike(searchKeyword, pageable);

        // then
        assertThat(searchResult).hasSize(0);
    }

    @DisplayName("save 메서드는")
    @Nested
    class Describe_save {

        @DisplayName("저장하려는 유저가 다른 유저를 팔로우한다면")
        @Nested
        class Context_UserFollowingOther {

            @DisplayName("추가된 Followings & Followers 정보 또한 함께 영속화시킨다.")
            @Test
            void follow_Valid_PersistenceSuccess() {
                // given
                User source = UserFactory.user("kevin");
                User target = UserFactory.user("danyee");
                userRepository.save(source);
                userRepository.save(target);
                source.follow(target);

                testEntityManager.flush();
                testEntityManager.clear();

                // when
                User findSource = userRepository.findById(source.getId())
                    .orElseThrow(IllegalArgumentException::new);
                User findTarget = userRepository.findById(target.getId())
                    .orElseThrow(IllegalArgumentException::new);

                // then
                assertThat(findSource.getFollowingCount()).isEqualTo(1);
                assertThat(findTarget.getFollowerCount()).isEqualTo(1);
            }
        }

        @DisplayName("영속화된 유저가 다른 유저를 언팔로우하면")
        @Nested
        class Context_UserUnfollowingOther {

            @DisplayName("변경된 Followings & Followers 정보도 변경 감지 대상이 된다.")
            @Test
            void unfollow_Valid_PersistenceSuccess() {
                // given
                User source = UserFactory.user("kevin");
                User target = UserFactory.user("danyee");
                userRepository.save(source);
                userRepository.save(target);
                source.follow(target);

                testEntityManager.flush();
                testEntityManager.clear();

                // when
                User findSource = userRepository.findById(source.getId())
                    .orElseThrow(IllegalArgumentException::new);
                User findTarget = userRepository.findById(target.getId())
                    .orElseThrow(IllegalArgumentException::new);
                int beforeFollowingCounts = findSource.getFollowingCount();
                int beforeFollowerCounts = findTarget.getFollowerCount();
                findSource.unfollow(findTarget);

                testEntityManager.flush();
                testEntityManager.clear();

                User findSource2 = userRepository.findById(source.getId())
                    .orElseThrow(IllegalArgumentException::new);
                User findTarget2 = userRepository.findById(target.getId())
                    .orElseThrow(IllegalArgumentException::new);

                // then
                assertThat(beforeFollowingCounts).isOne();
                assertThat(beforeFollowerCounts).isOne();
                assertThat(findSource2.getFollowingCount()).isZero();
                assertThat(findTarget2.getFollowerCount()).isZero();
            }
        }
    }

    @DisplayName("delete 메서드는")
    @Nested
    class Describe_delete {

        @DisplayName("팔로우 중인 Target User를 삭제하면")
        @Nested
        class Context_FollowingUserDeleted {

            @DisplayName("Source User의 팔로잉 정보 또한 변경된다.")
            @Test
            void follow_WhenFollowingDeleted_InformationUpdated() {
                // given
                User source = UserFactory.user("kevin");
                User target = UserFactory.user("danyee");
                userRepository.save(source);
                userRepository.save(target);
                source.follow(target);

                testEntityManager.flush();
                testEntityManager.clear();

                // when
                int comparableFollowingCounts = userRepository.findById(source.getId())
                    .orElseThrow(IllegalArgumentException::new)
                    .getFollowingCount();

                testEntityManager.clear();

                userRepository.deleteById(target.getId());

                testEntityManager.flush();
                testEntityManager.clear();

                User findSource = userRepository.findById(source.getId())
                    .orElseThrow(IllegalArgumentException::new);

                // then
                assertThat(comparableFollowingCounts).isOne();
                assertThat(findSource.getFollowingCount()).isZero();
            }
        }

        @DisplayName("팔로워인 Source User를 삭제하면")
        @Nested
        class Context_FollowerUserDeleted {

            @DisplayName("Target User의 팔로워 정보 또한 변경된다.")
            @Test
            void follow_WhenFollowerDeleted_InformationUpdated() {
                // given
                User source = UserFactory.user("kevin");
                User target = UserFactory.user("danyee");
                userRepository.save(source);
                userRepository.save(target);
                source.follow(target);

                testEntityManager.flush();
                testEntityManager.clear();

                // when
                int comparableFollowerCounts = userRepository.findById(target.getId())
                    .orElseThrow(IllegalArgumentException::new)
                    .getFollowerCount();

                testEntityManager.clear();

                userRepository.deleteById(source.getId());

                testEntityManager.flush();
                testEntityManager.clear();

                User findTarget = userRepository.findById(target.getId())
                    .get();

                // then
                assertThat(comparableFollowerCounts).isOne();
                assertThat(findTarget.getFollowerCount()).isZero();
            }
        }

        @DisplayName("특정 유저를 삭제하면")
        @Nested
        class Context_AnyUserDeleted {

            @DisplayName("해당 유저가 작성한 게시물 또한 삭제된다.")
            @Test
            void deleteUser_RelatedPostsRemoved_True() {
                // given
                User user = userRepository.findByBasicProfile_Name("testUser")
                    .orElseThrow(IllegalArgumentException::new);
                Post post = Post.builder().author(user).build();
                Post post1 = Post.builder().author(user).build();
                Post post2 = Post.builder().author(user).build();
                postRepository.save(post);
                postRepository.save(post1);
                postRepository.save(post2);

                testEntityManager.flush();
                testEntityManager.clear();

                // when
                List<Post> beforePosts = testEntityManager.getEntityManager()
                    .createQuery("select p from Post p where p.user = :user", Post.class)
                    .setParameter("user", user)
                    .getResultList();
                userRepository.deleteById(user.getId());

                testEntityManager.flush();
                testEntityManager.clear();

                List<Post> afterPosts = testEntityManager.getEntityManager()
                    .createQuery("select p from Post p where p.user = :user", Post.class)
                    .setParameter("user", user)
                    .getResultList();

                // then
                assertThat(beforePosts).hasSize(3);
                assertThat(afterPosts).isEmpty();
            }
        }
    }

    @DisplayName("searchFollowingsOf 메서드는")
    @Nested
    class Describe_searchFollowingsOf {

        @DisplayName("특정 유저가 팔로잉하는 유저가 없다면")
        @Nested
        class Context_NoFollowingsAvailable {

            @DisplayName("빈 리스트를 반환한다.")
            @Test
            void searchFollowingsOf_NoFollowings_EmptyList() {
                // given
                User user1 = UserFactory.user("user1");
                Pageable pageable = PageRequest.of(0, 2);

                userRepository.save(user1);

                testEntityManager.flush();
                testEntityManager.clear();

                // when
                List<User> followings = userRepository.searchFollowingsOf(user1, pageable);

                // then
                assertThat(followings).isEmpty();
            }
        }

        @DisplayName("특정 유저가 팔로잉하는 유저가 있다면")
        @Nested
        class Context_FollowingsAvailable {

            @DisplayName("페이징 조건에 맞춰 팔로잉중인 유저 리스트를 반환한다.")
            @Test
            void searchFollowingsOf_FollowingsAvailable_Pageable() {
                // given
                User user1 = UserFactory.user("user1");
                User user2 = UserFactory.user("user2");
                User user3 = UserFactory.user("user3");
                User user4 = UserFactory.user("user4");
                Pageable pageable = PageRequest.of(0, 2);

                userRepository.save(user1);
                userRepository.save(user2);
                userRepository.save(user3);
                userRepository.save(user4);

                user1.follow(user2);
                user1.follow(user3);
                user1.follow(user4);

                testEntityManager.flush();
                testEntityManager.clear();

                // when
                List<User> followings = userRepository.searchFollowingsOf(user1, pageable);

                // then
                assertThat(followings)
                    .extracting("basicProfile")
                    .extracting("name")
                    .containsExactly("user2", "user3")
                    .hasSize(2);
            }
        }
    }

    @DisplayName("searchFollowersOf 메서드는")
    @Nested
    class Describe_searchFollowersOf {

        @DisplayName("특정 유저를 팔로우하는 팔로워 유저가 없다면")
        @Nested
        class Context_NoFollowersAvailable {

            @DisplayName("빈 리스트를 반환한다.")
            @Test
            void searchFollowersOf_NoFollowers_EmptyList() {
                // given
                User user1 = UserFactory.user("user1");
                Pageable pageable = PageRequest.of(0, 2);

                userRepository.save(user1);

                testEntityManager.flush();
                testEntityManager.clear();

                // when
                List<User> followers = userRepository.searchFollowersOf(user1, pageable);

                // then
                assertThat(followers).isEmpty();
            }
        }

        @DisplayName("특정 유저를 팔로우하는 팔로워 유저가 있다면")
        @Nested
        class Context_FollowersAvailable {

            @DisplayName("페이징 조건에 맞춰 팔로워 유저 리스트를 반환한다.")
            @Test
            void searchFollowersOf_FollowersAvailable_Pageable() {
                // given
                User user1 = UserFactory.user("user1");
                User user2 = UserFactory.user("user2");
                User user3 = UserFactory.user("user3");
                User user4 = UserFactory.user("user4");
                Pageable pageable = PageRequest.of(0, 2);

                userRepository.save(user1);
                userRepository.save(user2);
                userRepository.save(user3);
                userRepository.save(user4);

                user1.follow(user4);
                user2.follow(user4);
                user3.follow(user4);

                testEntityManager.flush();
                testEntityManager.clear();

                // when
                List<User> followers = userRepository.searchFollowersOf(user4, pageable);

                // then
                assertThat(followers)
                    .extracting("basicProfile")
                    .extracting("name")
                    .containsExactly("user1", "user2")
                    .hasSize(2);
            }
        }
    }
}
