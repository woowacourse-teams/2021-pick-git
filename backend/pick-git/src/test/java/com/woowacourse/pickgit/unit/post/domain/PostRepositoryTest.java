package com.woowacourse.pickgit.unit.post.domain;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.JpaTestConfiguration;
import com.woowacourse.pickgit.post.application.PostDtoAssembler;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.comment.Comment;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

    @DisplayName("Post에 Comment를 추가하면 Comment가 자동 영속화된다.")
    @Test
    void addComment_WhenSavingPost_CommentSavedTogether() {
        // given
        User testUser = UserFactory.user("testUser");
        User savedTestUser = userRepository.save(testUser);

        Post post = Post.builder()
            .content("testContent")
            .githubRepoUrl("https://github.com/bperhaps")
            .author(savedTestUser)
            .build();

        // when
        Comment comment = new Comment("test comment", testUser);
        post.addComment(comment);

        postRepository.save(post);
        flushAndClear();

        // then
        Post findPost = postRepository.findById(post.getId())
            .orElseThrow(IllegalArgumentException::new);

        List<Comment> comments = findPost.getComments();
        assertThat(comments).hasSize(1);
    }

    @DisplayName("Tag를 기반으로 Post를 불러온다.")
    @ParameterizedTest
    @MethodSource("getPostsAndTagsForSearchPostByTagName")
    void findAllPostsByTagNamesfindAllPostsByTagNames_findAllPostsByTagNames_Success(
        List<Post> posts,
        User user,
        List<Tag> tags,
        String keyword,
        List<PostResponseDto> expected
    ) {
        userRepository.save(user);
        tags.forEach(tagRepository::save);
        posts.forEach(postRepository::save);

        PageRequest pageRequest = PageRequest.of(0, 3);
        List<String> keywords = List.of(keyword.split(" "));
        List<Post> savedPosts = postRepository.findAllPostsByTagNames(keywords, pageRequest);

        List<PostResponseDto> actual =
            PostDtoAssembler.assembleFrom(user, false, savedPosts);

        actual.sort(comparing(PostResponseDto::getAuthorName));
        expected.sort(comparing(PostResponseDto::getAuthorName));

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("id", "createdAt", "updatedAt")
            .isEqualTo(expected);
    }

    public static Stream<Arguments> getPostsAndTagsForSearchPostByTagName() {
        return Stream.of(
            createArgument("tag1", 0),
            createArgument("tag1 tag9", 0, 2),
            createArgument("tag3 tag1", 0),
            createArgument("tag1 tag4", 0, 1),
            createArgument("tag10"),
            createArgument("tag10 tag1", 0)
        );
    }

    private static Arguments createArgument(String keyword, int... expectedIndex) {
        User user = UserFactory.user("testUser");

        Tag[] tags = IntStream.rangeClosed(1, 9)
            .mapToObj(i -> new Tag(String.format("tag%d", i)))
            .toArray(Tag[]::new);

        Post[] posts = new Post[]{
            Post.builder()
                .content("testContent1")
                .githubRepoUrl("https://github.com/bperhaps1")
                .author(user)
                .tags(tags[0], tags[1], tags[2])
                .build(),
            Post.builder()
                .content("testContent2")
                .githubRepoUrl("https://github.com/bperhaps2")
                .author(user)
                .tags(tags[3], tags[4], tags[5])
                .build(),
            Post.builder()
                .content("testContent3")
                .githubRepoUrl("https://github.com/bperhaps3")
                .author(user)
                .tags(tags[6], tags[7], tags[8])
                .build()
        };

        List<Post> expected = Arrays.stream(expectedIndex)
            .mapToObj(i -> posts[i])
            .collect(toList());

        return Arguments.of(
            List.of(posts[0], posts[1], posts[2]),
            user,
            List.of(tags),
            keyword,
            PostDtoAssembler.assembleFrom(user, false, expected)
        );
    }

    private void flushAndClear() {
        testEntityManager.flush();
        testEntityManager.clear();
    }
}

