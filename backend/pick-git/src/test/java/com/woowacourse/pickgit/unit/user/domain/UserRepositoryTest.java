package com.woowacourse.pickgit.unit.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.user.InvalidUserException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.post.domain.content.PostContent;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

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
        users
            .stream()
            .forEach(user -> userRepository.save(user));
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
            .ignoringFields("id")
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

    @DisplayName("특정 User를 Follow할 때")
    @Nested
    class Describe_FollowOtherUser {

        @DisplayName("User가 영속화되면")
        @Nested
        class Context_WhenUserSaved {

            @DisplayName("Follow 정보 또한 함께 영속화된다.")
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

        @DisplayName("팔로우 중인 Target User가 삭제되면")
        @Nested
        class Context_WhenFollowingUserDeleted {

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

        @DisplayName("팔로워인 Source User가 삭제되면")
        @Nested
        class Context_WhenFollowerUserDeleted {

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
    }

    @DisplayName("특정 유저를 언팔로우 하면 변경된 팔로우 및 팔로잉 정보가 함께 영속화된다.")
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

    @DisplayName("특정 유저가 삭제되면 해당 유저가 작성한 게시물 또한 삭제된다.")
    @Test
    void deleteUser_RelatedPostsRemoved_True() {
        // given
        User user = userRepository.findByBasicProfile_Name("testUser")
            .orElseThrow(IllegalArgumentException::new);
        Post post = new Post(new Images(new ArrayList<>()), new PostContent("hi"), "url", user);
        Post post1 = new Post(new Images(new ArrayList<>()), new PostContent("hi"), "url", user);
        Post post2 = new Post(new Images(new ArrayList<>()), new PostContent("hi"), "url", user);
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
