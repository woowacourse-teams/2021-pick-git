package com.woowacourse.pickgit.unit.post.domain;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.JpaTestConfiguration;
import com.woowacourse.pickgit.post.application.dto.PostDtoAssembler;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

@Import(JpaTestConfiguration.class)
@DataJpaTest
public class PostRepositoryTest_search {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @DisplayName("Tag를 기반으로 Post를 불러온다.")
    @ParameterizedTest
    @MethodSource("getPostsAndTagsForSearchPostByTagName")
    void findAllPostsByTagNamesFindAllPostsByTagNames_findAllPostsByTagNames_Success(
        List<Post> posts,
        User user,
        List<Tag> tags,
        String keyword,
        List<PostResponseDto> expected
    ) {
        userRepository.save(user);
        tags.forEach(tagRepository::save);
        posts.forEach(postRepository::save);

        testEntityManager.flush();
        testEntityManager.clear();

        PageRequest pageRequest = PageRequest.of(0, 3);
        List<String> keywords = List.of(keyword.split(" "));
        List<Post> savedPosts = postRepository.findAllPostsByTagNames(keywords, pageRequest);

        List<PostResponseDto> actual =
            PostDtoAssembler.postResponseDtos(user, savedPosts);

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
            PostDtoAssembler.postResponseDtos(user, expected)
        );
    }

}
