package com.woowacourse.pickgit.unit.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.JpaTestConfiguration;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;

@Import(JpaTestConfiguration.class)
@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @DisplayName("게시글을 저장한다.")
    @Test
    void save_SavedPost_Success() {
        // given
        User savedTestUser = userRepository.save(
            UserFactory.user("testUser")
        );

        Post post = Post.builder()
            .content("test")
            .author(savedTestUser)
            .build();

        // when
        Post savedPost = postRepository.save(post);
        flushAndClear();

        Post actual = postRepository.findById(savedPost.getId())
            .orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(actual.getId()).isEqualTo(savedPost.getId());
    }

    @DisplayName("게시글을 저장하면 자동으로 생성 날짜가 주입된다.")
    @Test
    void save_SavedPostWithCreatedDate_Success() {
        // given
        User savedTestUser = userRepository.save(
            UserFactory.user("testUser")
        );

        Post post = Post.builder()
            .content("test")
            .author(savedTestUser)
            .build();

        // when
        Post savedPost = postRepository.save(post);

        // then
        assertThat(savedPost.getCreatedAt()).isNotNull();
        assertThat(savedPost.getCreatedAt()).isBefore(LocalDateTime.now());
    }

    @DisplayName("Post을 저장할 때 PostTag도 함께 영속화된다.")
    @Test
    void save_WhenSavingPost_TagSavedTogether() {
        //given
        User testUser = UserFactory.user("testUser");
        User savedTestUser = userRepository.save(testUser);

        Post post = Post.builder()
            .content("testContent")
            .githubRepoUrl("https://github.com/bperhaps")
            .author(savedTestUser)
            .build();
        Post savedPost = postRepository.save(post);

        Tag tag1 = new Tag("tag1");
        Tag tag2 = new Tag("tag2");

        tagRepository.save(tag1);
        tagRepository.save(tag2);

        // when
        post.addTags(List.of(tag1, tag2));
        flushAndClear();

        // then
        Post findPost = postRepository.findById(savedPost.getId())
            .orElse(null);

        assertThat(findPost).isNotNull();
        assertThat(findPost.getTagNames()).hasSize(2);
    }

    @DisplayName("Post를 저장할 때 Tag는 함께 영속화되지 않는다. (태그가 존재하지 않을 경우 예외가 발생한다)")
    @Test
    void save_WhenSavingPost_TagNotSavedTogether() {
        // given
        Post post = Post.builder()
            .content("abc")
            .build();
        List<Tag> tags = Arrays.asList(new Tag("tag1"), new Tag("tag2"));

        // when, then
        post.addTags(tags);
        assertThatThrownBy(() -> postRepository.save(post))
            .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @DisplayName("나를 포함하여 내가 팔로잉하는 유저들의 게시글을 최신순으로 가져온다.")
    @Test
    void findAllAssociatedPostsByUser_FollowingsLatestIncludingMe_Success() {
        // given - mockUsers
        User tester = UserFactory.user("tester");
        List<User> mockUsers = List.of(
            UserFactory.user("a1"),
            UserFactory.user("a2"),
            UserFactory.user("a3"),
            UserFactory.user("a4"),
            UserFactory.user("a5"),
            UserFactory.user("a6"),
            UserFactory.user("a7"),
            UserFactory.user("a8")
        );
        userRepository.save(tester);
        userRepository.saveAll(mockUsers);

        // given - follow
        int mockUserCounts = mockUsers.size();
        for (int i = 0; i < mockUserCounts; i += 2) {
            tester.follow(mockUsers.get(i));
        }

        // given - mockPosts
        List<Post> mockPosts = PostFactory.mockPostsBy(mockUsers);
        Post testerPost = PostFactory.mockPostBy(tester);
        postRepository.saveAll(mockPosts);
        postRepository.save(testerPost);

        flushAndClear();

        // when
        List<Post> feeds =
            postRepository.findAllAssociatedPostsByUser(tester, PageRequest.of(0, 10));

        // then
        assertThat(feeds)
            .extracting("user")
            .extracting("basicProfile")
            .extracting("name")
            .containsExactly("tester", "a7", "a5", "a3", "a1");
    }

    private void flushAndClear() {
        testEntityManager.flush();
        testEntityManager.clear();
    }
}

