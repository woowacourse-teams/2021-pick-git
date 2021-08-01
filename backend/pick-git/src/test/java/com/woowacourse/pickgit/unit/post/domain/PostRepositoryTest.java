package com.woowacourse.pickgit.unit.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.common.factory.PostBuilder;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.JpaTestConfiguration;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.post.domain.comment.Comment;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
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
import org.springframework.http.HttpStatus;

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

        Post post = new PostBuilder()
            .content("test")
            .user(savedTestUser)
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

        Post post = new PostBuilder()
            .content("test")
            .user(savedTestUser)
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

        Post post = new PostBuilder()
            .content("testContent")
            .githubRepoUrl("https://github.com/bperhaps")
            .user(savedTestUser)
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
        assertThat(findPost.getTags()).hasSize(2);
    }

    @DisplayName("Post를 저장할 때 Tag는 함께 영속화되지 않는다. (태그가 존재하지 않을 경우 예외가 발생한다)")
    @Test
    void save_WhenSavingPost_TagNotSavedTogether() {
        // given
        Post post = new PostBuilder()
            .content("abc")
            .build();
        List<Tag> tags = Arrays.asList(new Tag("tag1"), new Tag("tag2"));

        // when, then
        post.addTags(tags);
        assertThatThrownBy(() -> postRepository.save(post))
            .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @DisplayName("Post에 Comment를 추가하면 Comment가 자동 영속화된다.")
    @Test
    void addComment_WhenSavingPost_CommentSavedTogether() {
        // given
        User testUser = UserFactory.user("testUser");
        User savedTestUser = userRepository.save(testUser);

        Post post = new PostBuilder()
            .content("testContent")
            .githubRepoUrl("https://github.com/bperhaps")
            .user(savedTestUser)
            .build();

        // when
        Comment comment = new Comment("test comment");
        post.addComment(comment);

        postRepository.save(post);
        flushAndClear();

        // then
        Post findPost = postRepository.findById(post.getId())
            .orElseThrow(IllegalArgumentException::new);

        List<Comment> comments = findPost.getComments();
        assertThat(comments).hasSize(1);
    }

    @DisplayName("저장되어 있는 게시물 중 2번째 게시물을 조회한다.")
    @Test
    void findByIdAndUser_SecondPost_Success() {
        // given
        User user = UserFactory.user("testUser");
        userRepository.save(user);

        Post post1 = postBuilder("testContent1", "https://github.com/da-nyee/1", user);
        Post post2 = postBuilder("testContent2", "https://github.com/da-nyee/2", user);

        postRepository.save(post1);
        postRepository.save(post2);
        flushAndClear();

        // when
        User findUser = userRepository.findByBasicProfile_Name(user.getName())
            .orElseThrow(UserNotFoundException::new);
        Post findPost = postRepository.findByIdAndUser(post2.getId(), findUser)
            .orElseThrow(PostNotFoundException::new);

        // then
        assertThat(findPost)
            .usingRecursiveComparison()
            .ignoringFields("user")
            .isNotEqualTo(post1);
        assertThat(findPost)
            .usingRecursiveComparison()
            .ignoringFields("user")
            .isEqualTo(post2);
    }
    
    @DisplayName("저장되어 있지 않은 게시물은 조회할 수 없다. - 500 예외")
    @Test
    void findPostByIdAndUser_unsavedPost_500Exception() {
        // given
        User user = UserFactory.user("testUser");
        User savedUser = userRepository.save(user);

        Post post = postBuilder("testContent1", "https://github.com/da-nyee/1", savedUser);

        postRepository.save(post);
        flushAndClear();

        // when
        assertThatThrownBy(() -> {
            postRepository.findByIdAndUser(2L, savedUser)
                .orElseThrow(PostNotFoundException::new);
        }).isInstanceOf(PostNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("해당하는 게시물을 찾을 수 없습니다.");
    }

    private Post postBuilder(String content, String repoUrl, User savedUser) {
        return new PostBuilder()
            .content(content)
            .githubRepoUrl(repoUrl)
            .user(savedUser)
            .build();
    }

    private void flushAndClear() {
        testEntityManager.flush();
        testEntityManager.clear();
    }
}
