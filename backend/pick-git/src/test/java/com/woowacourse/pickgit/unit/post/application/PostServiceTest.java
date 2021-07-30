package com.woowacourse.pickgit.unit.post.application;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.PostBuilder;
import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.post.CommentFormatException;
import com.woowacourse.pickgit.exception.post.PostFormatException;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.exception.post.RepositoryParseException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.CommentResponse;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostImageUrlResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoriesResponseDto;
import com.woowacourse.pickgit.post.domain.PickGitStorage;
import com.woowacourse.pickgit.post.domain.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.request.CommentRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.HomeFeedRequest;
import com.woowacourse.pickgit.tag.application.TagService;
import com.woowacourse.pickgit.tag.application.TagsDto;
import com.woowacourse.pickgit.tag.domain.Tag;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PickGitStorage pickGitStorage;

    @Mock
    private PlatformRepositoryExtractor platformRepositoryExtractor;

    @Mock
    private TagService tagService;

    @DisplayName("사용자는 게시물을 등록할 수 있다.")
    @Test
    void write_LoginUser_Success() {
        // given
        PostRequestDto requestDto = PostFactory.mockPostRequestDtos().get(0);
        User user = UserFactory.user(1L, "testUser");
        Post post = new PostBuilder()
            .id(1L)
            .content("testContent")
            .images(extractImageUrlsFrom(requestDto))
            .build();

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class)))
            .willReturn(post);
        given(pickGitStorage.store(anyList(), anyString()))
            .willReturn(extractImageUrlsFrom(requestDto));
        given(tagService.findOrCreateTags(any()))
            .willReturn(extractTagsFrom(requestDto));

        // when
        PostImageUrlResponseDto responseDto = postService.write(requestDto);

        // then
        assertThat(responseDto.getId()).isNotNull();
        assertThat(responseDto.getImageUrls()).containsAll(extractImageUrlsFrom(requestDto));

        verify(userRepository, times(1))
            .findByBasicProfile_Name(requestDto.getUsername());
        verify(postRepository, times(1))
            .save(any(Post.class));
        verify(pickGitStorage, times(1))
            .store(anyList(), anyString());
        verify(tagService, times(1))
            .findOrCreateTags(any(TagsDto.class));
    }

    private List<String> extractImageUrlsFrom(PostRequestDto requestDto) {
        return requestDto.getImages().stream()
            .map(MultipartFile::getName)
            .map(name -> String.format("http://testImages.test/%s", name))
            .collect(toList());
    }

    private List<Tag> extractTagsFrom(PostRequestDto requestDto) {
        return requestDto.getTags().stream()
            .map(Tag::new)
            .collect(toList());
    }

    @DisplayName("사용자는 게시물을 등록할 수 있다.")
    @Test
    void write_InvalidUser_ExceptionOccur() {
        // given
        PostRequestDto requestDto = PostFactory.mockPostRequestDtos().get(0);

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.empty());

        // when then
        assertThatCode(() -> postService.write(requestDto))
            .isInstanceOf(UserNotFoundException.class)
            .extracting("errorCode")
            .isEqualTo("U0001");

        verify(userRepository, times(1))
            .findByBasicProfile_Name(requestDto.getUsername());
    }

    @DisplayName("컨텐츠의 길이가 500보다 크면 게시물을 등록할 수 없다.")
    @Test
    void write_ContentLengthOver500_Fail() {
        // given
        PostRequestDto requestDto = PostRequestDto.builder()
            .content("a".repeat(501))
            .build();

        // when then
        assertThatCode(() -> postService.write(requestDto))
            .isInstanceOf(PostFormatException.class);
    }

    @DisplayName("게시물에 댓글을 정상 등록한다.")
    @Test
    void addComment_ValidContent_Success() {
        //given
        String comment_content = "test comment";
        User user = UserFactory.user(1L, "testUser1");
        Post post = new PostBuilder()
            .id(1L)
            .user(user)
            .build();

        given(postRepository.findById(anyLong()))
            .willReturn(Optional.of(post));
        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));

        CommentRequest commentRequest =
            new CommentRequest(user.getName(), comment_content, post.getId());

        //when
        CommentResponse commentResponseDto = postService.addComment(commentRequest);

        //then
        assertThat(commentResponseDto.getAuthorName()).isEqualTo(user.getName());
        assertThat(commentResponseDto.getContent()).isEqualTo(comment_content);

        verify(postRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findByBasicProfile_Name(anyString());
    }

    @DisplayName("게시물에 빈 댓글을 등록할 수 없다.")
    @Test
    void addComment_InvalidContent_ExceptionThrown() {
        Post post = new PostBuilder()
            .id(1L)
            .build();

        User user = UserFactory.user("testuser");

        given(postRepository.findById(anyLong()))
            .willReturn(Optional.of(post));
        given(userRepository.findByBasicProfile_Name(user.getName()))
            .willReturn(Optional.of(user));

        CommentRequest commentRequest =
            new CommentRequest(user.getName(), "", post.getId());

        // then
        assertThatCode(() -> postService.addComment(commentRequest))
            .isInstanceOf(CommentFormatException.class)
            .extracting("errorCode")
            .isEqualTo("F0002");

        verify(postRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findByBasicProfile_Name(anyString());
    }

    @DisplayName("존재하지 않는 사용자는 댓글을 등록할 수 없다.")
    @Test
    void addComment_invalidUser_ExceptionOccur() {
        //given
        CommentRequest commentRequest =
            new CommentRequest("invalidUser", "comment_content", 1L);

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willThrow(new UserNotFoundException(
                "U0001",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "해당하는 사용자를 찾을 수 없습니다."
            ));

        //when then
        assertThatCode(() -> postService.addComment(commentRequest))
            .isInstanceOf(UserNotFoundException.class)
            .extracting("errorCode")
            .isEqualTo("U0001");

        verify(userRepository, times(1)).findByBasicProfile_Name(anyString());
    }

    @DisplayName("존재하지 않는 게시물에는 댓글을 등록할 수 없다.")
    @Test
    void addComment_invalidPost_ExceptionOccur() {
        //given
        CommentRequest commentRequest =
            new CommentRequest("testUser", "comment_content", 1L);

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(UserFactory.user()));
        given(postRepository.findById(anyLong()))
            .willThrow(new PostNotFoundException(
                "P0002",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "해당하는 게시물을 찾을 수 없습니다."
            ));

        //when then
        assertThatCode(() -> postService.addComment(commentRequest))
            .isInstanceOf(PostNotFoundException.class)
            .extracting("errorCode")
            .isEqualTo("P0002");

        verify(userRepository, times(1)).findByBasicProfile_Name(anyString());
        verify(postRepository, times(1)).findById(anyLong());
    }

    @DisplayName("사용자는 Repository 목록을 가져올 수 있다.")
    @Test
    void showRepositories_LoginUser_Success() {
        // given
        String accessToken = "bearer token";
        String userName = "testUserName";

        RepositoryRequestDto requestDto = new RepositoryRequestDto(accessToken, userName);
        List<RepositoryResponseDto> repositories = List.of(
            new RepositoryResponseDto("pick", "https://github.com/jipark3/pick"),
            new RepositoryResponseDto("git", "https://github.com/jipark3/git")
        );

        given(platformRepositoryExtractor
            .extract(requestDto.getToken(), requestDto.getUsername()))
            .willReturn(repositories);

        // when
        RepositoriesResponseDto repositoriesResponseDto = postService.showRepositories(requestDto);
        List<RepositoryResponseDto> responseDtos = repositoriesResponseDto.getRepositories();

        // then
        assertThat(responseDtos).containsAll(repositories);
        verify(platformRepositoryExtractor, times(1))
            .extract(requestDto.getToken(), requestDto.getUsername());
    }

    @DisplayName("AccessToken이 잘못되었다면, Repository 목록을 가져올 수 없다.")
    @Test
    void showRepositories_InvalidAccessToken_Fail() {
        // given
        String accessToken = "bearer invalid token";
        String userName = "testUserName";

        RepositoryRequestDto requestDto = new RepositoryRequestDto(accessToken, userName);

        given(platformRepositoryExtractor.extract(requestDto.getToken(), requestDto.getUsername()))
            .willThrow(new RepositoryParseException(
                "V0001",
                HttpStatus.BAD_REQUEST,
                "레포지토리 목록을 불러올 수 없습니다."
            ));

        // when then
        assertThatCode(() -> postService.showRepositories(requestDto))
            .isInstanceOf(RepositoryParseException.class);

        verify(platformRepositoryExtractor, times(1))
            .extract(requestDto.getToken(), requestDto.getUsername());
    }

    @DisplayName("UserName이 잘못되었다면, Repository 목록을 가져올 수 없다.")
    @Test
    void showRepositories_InvalidUserName_Fail() {
        // given
        String accessToken = "bearer test token";
        String userName = "invalidName";

        RepositoryRequestDto requestDto = new RepositoryRequestDto(accessToken, userName);

        given(platformRepositoryExtractor.extract(requestDto.getToken(), requestDto.getUsername()))
            .willThrow(new RepositoryParseException(
                "V0001",
                HttpStatus.BAD_REQUEST,
                "레포지토리 목록을 불러올 수 없습니다."
            ));

        // when then
        assertThatCode(() -> postService.showRepositories(requestDto))
            .isInstanceOf(RepositoryParseException.class);

        verify(platformRepositoryExtractor, times(1))
            .extract(requestDto.getToken(), requestDto.getUsername());
    }

    @DisplayName("메인 홈 피드를 가져온다.")
    @Test
    void readHomeFeed_getMainHomeFeed_success() {
        //given
        List<Post> posts = List.of(
            createPostOfId(1L),
            createPostOfId(2L),
            createPostOfId(3L)
        );

        HomeFeedRequest homeFeedRequest = HomeFeedRequest.builder()
            .appUser(new GuestUser())
            .page(1L)
            .limit(3L)
            .build();

        given(postRepository.findAllPosts(any(Pageable.class)))
            .willReturn(posts);

        //when
        List<PostResponseDto> postResponseDtos = postService.readHomeFeed(homeFeedRequest);

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
        return new PostBuilder()
            .id(id)
            .user(UserFactory.user())
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

        HomeFeedRequest homeFeedRequest = HomeFeedRequest.builder()
            .appUser(new LoginUser("testUser", "at"))
            .page(1L)
            .limit(3L)
            .build();

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(UserFactory.user("testUser")));
        given(postRepository.findAllPostsByUser(any(User.class), any(Pageable.class)))
            .willReturn(posts);

        //when
        List<PostResponseDto> postResponseDtos = postService.readMyFeed(homeFeedRequest);

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
        HomeFeedRequest homeFeedRequest = HomeFeedRequest.builder()
            .appUser(new LoginUser("testUser", "at"))
            .page(1L)
            .limit(3L)
            .build();

        //when
        assertThatCode(() -> postService.readMyFeed(homeFeedRequest))
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
        HomeFeedRequest homeFeedRequest = HomeFeedRequest.builder()
            .appUser(new GuestUser())
            .page(1L)
            .limit(3L)
            .build();

        //when
        assertThatCode(() -> postService.readMyFeed(homeFeedRequest))
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

        HomeFeedRequest homeFeedRequest = HomeFeedRequest.builder()
            .appUser(new LoginUser("loginUser", "at"))
            .page(1L)
            .limit(3L)
            .build();

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(UserFactory.user("testUser")));
        given(postRepository.findAllPostsByUser(any(User.class), any(Pageable.class)))
            .willReturn(posts);

        //when
        List<PostResponseDto> postResponseDtos =
            postService.readUserFeed(homeFeedRequest, "testUser");

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

    @DisplayName("게스트 유저는 다른 유저의 홈 피드를 가져온다")
    @Test
    void readUserFeed_guestUser_ExceptionOccur() {
        //given
        List<Post> posts = List.of(
            createPostOfId(1L),
            createPostOfId(2L),
            createPostOfId(3L)
        );

        HomeFeedRequest homeFeedRequest = HomeFeedRequest.builder()
            .appUser(new GuestUser())
            .page(1L)
            .limit(3L)
            .build();

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(UserFactory.user("testUser")));
        given(postRepository.findAllPostsByUser(any(User.class), any(Pageable.class)))
            .willReturn(posts);

        //when
        List<PostResponseDto> postResponseDtos =
            postService.readUserFeed(homeFeedRequest, "testUser");

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

        HomeFeedRequest homeFeedRequest = HomeFeedRequest.builder()
            .appUser(new LoginUser("invalidUser", "at"))
            .page(1L)
            .limit(3L)
            .build();

        given(userRepository.findByBasicProfile_Name("testUser"))
            .willReturn(Optional.of(UserFactory.user("testUser")));
        given(postRepository.findAllPostsByUser(any(User.class), any(Pageable.class)))
            .willReturn(posts);

        //when
        List<PostResponseDto> postResponseDtos =
            postService.readUserFeed(homeFeedRequest, "testUser");

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
}
