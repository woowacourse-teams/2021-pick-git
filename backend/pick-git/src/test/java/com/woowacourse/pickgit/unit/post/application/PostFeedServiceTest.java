package com.woowacourse.pickgit.unit.post.application;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
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
class PostFeedServiceTest {

    @InjectMocks
    private PostFeedService postFeedService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @DisplayName("게스트 유저 - 최신순의 메인 홈 피드를 가져온다. (좋아요 여부 null)")
    @Test
    void readHomeFeed_LatestAllWhenGuest_success() {
        //given
        List<Post> posts = List.of(
            createPostOfId(1L),
            createPostOfId(2L),
            createPostOfId(3L)
        );

        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .isGuest(true)
            .pageable(PageRequest.of(1, 3))
            .build();

        given(postRepository.findAllPosts(any(Pageable.class)))
            .willReturn(posts);

        //when
        List<PostResponseDto> postResponseDtos = postFeedService.allHomeFeed(homeFeedRequestDto);

        //then
        assertThat(postResponseDtos)
            .extracting("id", "liked")
            .containsExactly(
                tuple(1L, null),
                tuple(2L, null),
                tuple(3L, null)
            );

        verify(postRepository, times(1)).findAllPosts(any(Pageable.class));
    }

    @DisplayName("로그인 유저 - 팔로잉한 사람들의 최신 게시글 피드를 가져온다. (좋아요 여부 true/false)")
    @Test
    void followingHomeFeed_FollowingsWhenLogin_success() {
        //given
        List<Post> posts = List.of(
            createPostOfId(1L),
            createPostOfId(2L),
            createPostOfId(3L)
        );

        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .isGuest(false)
            .requestUserName("tester")
            .pageable(PageRequest.of(1, 3))
            .build();

        User tester = UserFactory.user(1L, "tester");
        posts.get(0).like(tester);

        given(userRepository.findByBasicProfile_Name(homeFeedRequestDto.getRequestUserName()))
            .willReturn(Optional.of(tester));
        given(postRepository.findAllAssociatedPostsByUser(eq(tester), any(Pageable.class)))
            .willReturn(posts);

        //when
        List<PostResponseDto> postResponseDtos = postFeedService.followingHomeFeed(homeFeedRequestDto);

        //then
        assertThat(postResponseDtos)
            .extracting("id", "liked")
            .containsExactly(
                tuple(1L, true),
                tuple(2L, false),
                tuple(3L, false)
            );

        verify(postRepository, times(1))
            .findAllAssociatedPostsByUser(eq(tester), any(Pageable.class));
    }

    @DisplayName("로그인 유저 - 최신 게시글 피드를 가져온다. (좋아요 여부 true/false)")
    @Test
    void allHomeFeed_allWhenLogin_success() {
        //given
        List<Post> posts = List.of(
            createPostOfId(1L),
            createPostOfId(2L),
            createPostOfId(3L)
        );

        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .isGuest(false)
            .requestUserName("tester")
            .pageable(PageRequest.of(1, 3))
            .build();

        User tester = UserFactory.user(1L, "tester");

        given(userRepository.findByBasicProfile_Name(homeFeedRequestDto.getRequestUserName()))
            .willReturn(Optional.of(tester));
        given(postRepository.findAllPosts(any(Pageable.class)))
            .willReturn(posts);

        //when
        List<PostResponseDto> postResponseDtos = postFeedService.allHomeFeed(homeFeedRequestDto);

        //then
        assertThat(postResponseDtos)
            .extracting("id", "liked")
            .containsExactly(
                tuple(1L, false),
                tuple(2L, false),
                tuple(3L, false)
            );

        verify(postRepository, times(1))
            .findAllPosts(any(Pageable.class));
    }

    private Post createPostOfId(long id) {
        return Post.builder()
            .id(id)
            .author(UserFactory.user())
            .content("test")
            .build();
    }

    @DisplayName("나의 홈 피드를 가져온다.")
    @Test
    void readMyFeed_getMyHomeFeed_success() {
        //given
        List<Post> posts = List.of(
            createPostOfId(1L),
            createPostOfId(2L),
            createPostOfId(3L)
        );

        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .requestUserName("testUser")
            .isGuest(false)
            .pageable(PageRequest.of(1, 3))
            .build();

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(UserFactory.user("testUser")));
        given(postRepository.findAllPostsByUser(any(User.class), any(Pageable.class)))
            .willReturn(posts);

        //when
        List<PostResponseDto> postResponseDtos = postFeedService.userFeed(homeFeedRequestDto, "testUser");

        //then
        List<Long> expected = posts.stream()
            .map(Post::getId)
            .collect(toList());

        List<Long> actual = postResponseDtos.stream()
            .map(PostResponseDto::getId)
            .collect(toList());

        assertThat(actual).containsAll(expected);
    }

    @DisplayName("잘못된 유저는 나의 홈 피드를 가져오지 못한다.")
    @Test
    void readMyFeed_invalidUser_ExceptionOccur() {
        //given
        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .requestUserName("testUser")
            .isGuest(false)
            .pageable(PageRequest.of(1, 3))
            .build();

        //when
        assertThatCode(() -> postFeedService.userFeed(homeFeedRequestDto, "testUser"))
            .isInstanceOf(UserNotFoundException.class)
            .extracting("errorCode")
            .isEqualTo("U0001");

        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
    }

    @DisplayName("다른 유저의 홈 피드를 가져온다")
    @Test
    void readUserFeed_validUser_ExceptionOccur() {
        //given
        List<Post> posts = List.of(
            createPostOfId(1L),
            createPostOfId(2L),
            createPostOfId(3L)
        );

        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .requestUserName("loginUser")
            .isGuest(false)
            .pageable(PageRequest.of(1, 3))
            .build();

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(UserFactory.user("testUser")));
        given(postRepository.findAllPostsByUser(any(User.class), any(Pageable.class)))
            .willReturn(posts);

        //when
        List<PostResponseDto> postResponseDtos =
            postFeedService.userFeed(homeFeedRequestDto, "testUser");

        //then
        List<Long> expected = posts.stream()
            .map(Post::getId)
            .collect(toList());

        List<Long> actual = postResponseDtos.stream()
            .map(PostResponseDto::getId)
            .collect(toList());

        assertThat(actual).containsAll(expected);

        verify(userRepository, times(2)).findByBasicProfile_Name(anyString());
    }

    @DisplayName("게스트 유저는 다른 유저의 홈 피드를 가져온다")
    @Test
    void readUserFeed_guestUser_ExceptionOccur() {
        //given
        List<Post> posts = List.of(
            createPostOfId(1L),
            createPostOfId(2L),
            createPostOfId(3L)
        );

        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .isGuest(true)
            .pageable(PageRequest.of(1, 3))
            .build();

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(UserFactory.user("testUser")));
        given(postRepository.findAllPostsByUser(any(User.class), any(Pageable.class)))
            .willReturn(posts);

        //when
        List<PostResponseDto> postResponseDtos =
            postFeedService.userFeed(homeFeedRequestDto, "testUser");

        //then
        List<Long> expected = posts.stream()
            .map(Post::getId)
            .collect(toList());

        List<Long> actual = postResponseDtos.stream()
            .map(PostResponseDto::getId)
            .collect(toList());

        assertThat(actual).containsAll(expected);

        verify(userRepository, times(1)).findByBasicProfile_Name(anyString());
    }

    @DisplayName("잘못된 유저는 다른 유저의 홈 피드를 가져온다")
    @Test
    void readUserFeed_invalidUser_ExceptionOccur() {
        //given
        List<Post> posts = List.of(
            createPostOfId(1L),
            createPostOfId(2L),
            createPostOfId(3L)
        );

        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .requestUserName("invalidUser")
            .isGuest(false)
            .pageable(PageRequest.of(1, 3))
            .build();

        given(userRepository.findByBasicProfile_Name("testUser"))
            .willReturn(Optional.of(UserFactory.user("testUser")));
        given(userRepository.findByBasicProfile_Name("invalidUser"))
            .willReturn(Optional.of(UserFactory.user("invalidUser")));
        given(postRepository.findAllPostsByUser(any(User.class), any(Pageable.class)))
            .willReturn(posts);

        //when
        List<PostResponseDto> postResponseDtos =
            postFeedService.userFeed(homeFeedRequestDto, "testUser");

        //then
        List<Long> expected = posts.stream()
            .map(Post::getId)
            .collect(toList());

        List<Long> actual = postResponseDtos.stream()
            .map(PostResponseDto::getId)
            .collect(toList());

        assertThat(actual).containsAll(expected);

        verify(userRepository, times(2)).findByBasicProfile_Name(anyString());
    }
}
