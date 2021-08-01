package com.woowacourse.pickgit.unit.post.application;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class PostFeedServiceTest {
    @InjectMocks
    private PostFeedService postFeedService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @DisplayName("메인 홈 피드를 가져온다.")
    @Test
    void readHomeFeed_getMainHomeFeed_success() {
        //given
        List<Post> posts = List.of(
            createPostOfId(1L),
            createPostOfId(2L),
            createPostOfId(3L)
        );

        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .isGuest(true)
            .page(1L)
            .limit(3L)
            .build();

        given(postRepository.findAllPosts(any(Pageable.class)))
            .willReturn(posts);

        //when
        List<PostResponseDto> postResponseDtos = postFeedService.homeFeed(homeFeedRequestDto);

        //then
        List<Long> expected = posts.stream()
            .map(Post::getId)
            .collect(toList());

        List<Long> actual = postResponseDtos.stream()
            .map(PostResponseDto::getId)
            .collect(toList());

        assertThat(actual).containsAll(expected);
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
            .page(1L)
            .limit(3L)
            .build();

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(UserFactory.user("testUser")));
        given(postRepository.findAllPostsByUser(any(User.class), any(Pageable.class)))
            .willReturn(posts);

        //when
        List<PostResponseDto> postResponseDtos = postFeedService.myFeed(homeFeedRequestDto);

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
            .page(1L)
            .limit(3L)
            .build();

        //when
        assertThatCode(() -> postFeedService.myFeed(homeFeedRequestDto))
            .isInstanceOf(UserNotFoundException.class)
            .extracting("errorCode")
            .isEqualTo("U0001");

        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
    }

    @DisplayName("게스트 유저는 나의 홈 피드를 가져오지 못한다.")
    @Test
    void readMyFeed_guestUser_ExceptionOccur() {
        //given
        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .isGuest(true)
            .page(1L)
            .limit(3L)
            .build();

        //when
        assertThatCode(() -> postFeedService.myFeed(homeFeedRequestDto))
            .isInstanceOf(UnauthorizedException.class)
            .extracting("errorCode")
            .isEqualTo("A0002");
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
            .page(1L)
            .limit(3L)
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
            .page(1L)
            .limit(3L)
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
            .page(1L)
            .limit(3L)
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
