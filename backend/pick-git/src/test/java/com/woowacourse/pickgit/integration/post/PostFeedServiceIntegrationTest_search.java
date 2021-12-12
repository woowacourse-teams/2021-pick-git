package com.woowacourse.pickgit.integration.post;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.post.IllegalSearchTypeException;
import com.woowacourse.pickgit.integration.IntegrationTest;
import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.dto.PostDtoAssembler;
import com.woowacourse.pickgit.post.application.dto.request.SearchPostsRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

public class PostFeedServiceIntegrationTest_search extends IntegrationTest {

    @Autowired
    private PostFeedService postFeedService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    User user = UserFactory.user();

    @BeforeEach
    void setUp() {
        userRepository.save(user);

        Tag[] tags = new Tag[]{
            new Tag("tag1"),
            new Tag("tag2"),
            new Tag("tag3"),
            new Tag("tag4"),
            new Tag("tag5")
        };

        List.of(tags).forEach(tagRepository::save);

        List<Tag[]> tagsForPost = List.of(
            new Tag[]{tags[0], tags[1]},
            new Tag[]{tags[0]},
            new Tag[]{tags[0], tags[2]},
            new Tag[]{tags[1], tags[4]},
            new Tag[]{tags[3]}
        );

        tagsForPost.forEach(this::createPost);
    }

    private void createPost(Tag... tags) {
        Post post = Post.builder()
            .content("content1")
            .githubRepoUrl("github")
            .tags(tags)
            .author(user)
            .build();

        postRepository.save(post);
    }

    @DisplayName("유저는 Tag로 게시물을 검색할 수 있다.")
    @ParameterizedTest
    @MethodSource("getPostSearchArguments")
    void userCanFindPostViaTags(String keyword) {
        // given
        User user = UserFactory.user();

        SearchPostsRequestDto searchPostsRequestDto =
            createSearchPostsRequestDto("tags", keyword, "testUser", false);

        // when
        List<PostResponseDto> actual = postFeedService
            .search(searchPostsRequestDto, PageRequest.of(0, 5));
        actual.sort(comparing(PostResponseDto::getId));

        // then
        List<Post> allPosts = postRepository.findAll();
        List<PostResponseDto> allPostResponseDtos = PostDtoAssembler
            .postResponseDtos(user, allPosts);
        List<PostResponseDto> expected = findPostResponseByTags(allPostResponseDtos, keyword);

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @DisplayName("게스트는 Tag로 게시물을 검색할 수 있다.")
    @ParameterizedTest
    @MethodSource("getPostSearchArguments")
    void guestCanFindPostViaTags(String keyword) {
        // given
        SearchPostsRequestDto searchPostsRequestDto =
            createSearchPostsRequestDto("tags", keyword, null, true);

        // when
        List<PostResponseDto> actual = postFeedService
            .search(searchPostsRequestDto, PageRequest.of(0, 5));
        actual.sort(comparing(PostResponseDto::getId));

        // then
        List<Post> allPosts = postRepository.findAll();
        List<PostResponseDto> allPostResponseDtos = PostDtoAssembler
            .postResponseDtos(null, allPosts);
        List<PostResponseDto> expected = findPostResponseByTags(allPostResponseDtos, keyword);

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private static Stream<Arguments> getPostSearchArguments() {
        return Stream.of(
            Arguments.of("tag1"),
            Arguments.of("tag1 tag3"),
            Arguments.of(""),
            Arguments.of("tag19"),
            Arguments.of("tag4"),
            Arguments.of("tag4 tag1 tag3"),
            Arguments.of("tag1 tag2 tag3"),
            Arguments.of("tag1 tag19")
        );
    }

    private List<PostResponseDto> findPostResponseByTags(List<PostResponseDto> posts,
        String keyword) {
        List<String> keywords = List.of(keyword.split(" "));
        return posts.stream()
            .filter(post -> isMatched(keywords, post.getTags()))
            .sorted(comparing(PostResponseDto::getId))
            .collect(toList());
    }

    private boolean isMatched(List<String> keywords, List<String> tags) {
        return keywords.stream()
            .anyMatch(tags::contains);
    }

    @DisplayName("존재하지 않는 type을 요청하면 예외가 발생한다.")
    @ParameterizedTest
    @CsvSource(value = {"user, false, invalidTag", "user, false, invalidTag"})
    void userCanFindPostViaTags(String userName, boolean isGuest, String type) {
        // given
        SearchPostsRequestDto searchPostsRequestDto =
            createSearchPostsRequestDto(type, "keyword", userName, isGuest);

        // when then
        assertThatThrownBy(
            () -> postFeedService.search(searchPostsRequestDto, PageRequest.of(0, 5)))
            .isInstanceOf(IllegalSearchTypeException.class)
            .extracting("errorCode")
            .isEqualTo("P0006");
    }

    private SearchPostsRequestDto createSearchPostsRequestDto(
        String type,
        String keyword,
        String userName,
        boolean isGuest
    ) {
        return SearchPostsRequestDto.builder()
            .type(type)
            .keyword(keyword)
            .userName(userName)
            .isGuest(isGuest)
            .build();
    }
}
