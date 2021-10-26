package com.woowacourse.pickgit.unit.post.application;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.dto.PostDtoAssembler;
import com.woowacourse.pickgit.post.application.dto.request.SearchPostsRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.search.SearchTypes;
import com.woowacourse.pickgit.post.application.search.type.TagType;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class PostFeedService_search {

    @InjectMocks
    private PostFeedService postFeedService;

    @Mock
    private SearchTypes searchTypes;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @DisplayName("정상 유저는 Tag기반으로 게시물을 검색한다.")
    @Test
    void search_findAllPostsByTagNamesWithUser_Success() {
        //given
        final User requestUser = UserFactory.user("requestUser");
        final User writer = UserFactory.user("writer");
        final String type = "tags";

        Post post = createPost(writer, "tag1");

        given(searchTypes.findByTypeName("tags"))
            .willReturn(new TagType(postRepository));
        given(postRepository.findAllPostsByTagNames(anyList(), any(Pageable.class)))
            .willReturn(List.of(post));
        given(userRepository.findByBasicProfile_Name(requestUser.getName()))
            .willReturn(Optional.of(writer));

        //when
        SearchPostsRequestDto searchPostsRequestDto =
            createSearchPostsRequestDto(type, requestUser.getName(), false);
        List<PostResponseDto> actual =
            postFeedService.search(searchPostsRequestDto, PageRequest.of(0, 5));

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(PostDtoAssembler.postResponseDtos(requestUser, List.of(post)));

        verify(searchTypes, times(1))
            .findByTypeName(type);
        verify(postRepository, times(1))
            .findAllPostsByTagNames(anyList(), any(Pageable.class));
        verify(userRepository, times(1))
            .findByBasicProfile_Name(requestUser.getName());
    }

    @DisplayName("게스트 유저는 Tag기반으로 게시물을 검색한다.")
    @Test
    void search_findAllPostsByTagNamesWithGuest_Success() {
        // given
        final User writer = UserFactory.user("writer");
        final String type = "tags";

        Post post = createPost(writer, "tag1");

        given(searchTypes.findByTypeName("tags"))
            .willReturn(new TagType(postRepository));
        given(postRepository.findAllPostsByTagNames(anyList(), any(Pageable.class)))
            .willReturn(List.of(post));

        // when
        SearchPostsRequestDto searchPostsRequestDto =
            createSearchPostsRequestDto(type, null, true);
        List<PostResponseDto> actual =
            postFeedService.search(searchPostsRequestDto, PageRequest.of(0, 5));

        // then
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(PostDtoAssembler.postResponseDtos(null, List.of(post)));

        verify(searchTypes, times(1))
            .findByTypeName(type);
        verify(postRepository, times(1))
            .findAllPostsByTagNames(anyList(), any(Pageable.class));
    }

    private static Post createPost(User user, String... tags) {
        return Post.builder()
            .id(1L)
            .githubRepoUrl("githubUrl")
            .author(user)
            .content("content")
            .images(List.of("imageUrl"))
            .tags(Arrays.stream(tags).map(Tag::new).collect(toList()))
            .build();
    }

    private SearchPostsRequestDto createSearchPostsRequestDto(
        String type,
        String userName,
        boolean isGuest
    ) {
        return SearchPostsRequestDto.builder()
            .type(type)
            .keyword("keyword")
            .userName(userName)
            .isGuest(isGuest)
            .build();
    }
}
