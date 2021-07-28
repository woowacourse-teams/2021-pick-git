package com.woowacourse.pickgit.unit.post.application;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.PostBuilder;
import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.post.CannotUnlikeException;
import com.woowacourse.pickgit.exception.post.CommentFormatException;
import com.woowacourse.pickgit.exception.post.DuplicatedLikeException;
import com.woowacourse.pickgit.exception.post.PostFormatException;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.exception.post.RepositoryParseException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.CommentResponse;
import com.woowacourse.pickgit.post.application.dto.request.PostDeleteRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostUpdateRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostImageUrlResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostUpdateResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoriesResponseDto;
import com.woowacourse.pickgit.post.domain.PickGitStorage;
import com.woowacourse.pickgit.post.domain.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.request.CommentRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.HomeFeedRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.PostUpdateRequest;
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

    @DisplayName("사용자는 특정 게시물을 좋아요 할 수 있다.")
    @Test
    void like_ValidUser_Success() {
        // given
        AppUser appUser = new LoginUser("test user", "token");
        Long postId = 1L;

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(UserFactory.user(1L, appUser.getUsername())));
        given(postRepository.findById(anyLong()))
            .willReturn(Optional.of(
                new PostBuilder()
                    .id(postId)
                    .content("abc")
                    .build())
            );

        // when
        LikeResponseDto likeResponseDto = postService.like(appUser, postId);

        // then
        assertThat(likeResponseDto.getLikeCount()).isEqualTo(1);
        assertThat(likeResponseDto.isLiked()).isTrue();

        verify(userRepository, times(1))
            .findByBasicProfile_Name(appUser.getUsername());
        verify(postRepository, times(1))
            .findById(postId);
    }

    @DisplayName("사용자는 특정 게시물을 좋아요 취소 할 수 있다.")
    @Test
    void unlike_ValidUser_Success() {
        // given
        AppUser appUser = new LoginUser("test user", "token");
        Long postId = 1L;
        User user = UserFactory.user(1L, appUser.getUsername());
        Post post = new PostBuilder()
            .id(postId)
            .content("abc")
            .build();

        post.like(user);

        given(userRepository.findByBasicProfile_Name(appUser.getUsername()))
            .willReturn(Optional.of(user));
        given(postRepository.findById(postId))
            .willReturn(Optional.of(post));

        // when
        LikeResponseDto likeResponseDto = postService.unlike(appUser, postId);

        // then
        assertThat(likeResponseDto.getLikeCount()).isEqualTo(0);
        assertThat(likeResponseDto.isLiked()).isFalse();

        verify(userRepository, times(1))
            .findByBasicProfile_Name(appUser.getUsername());
        verify(postRepository, times(1))
            .findById(anyLong());
    }

    @DisplayName("사용자는 이미 좋아요 한 게시물을 좋아요 추가 할 수 없다.")
    @Test
    void like_DuplicatedLike_400ExceptionThrown() {
        // given
        AppUser appUser = new LoginUser("test user", "token");
        Long postId = 1L;
        User user = UserFactory.user(1L, appUser.getUsername());
        Post post = new PostBuilder()
            .id(postId)
            .content("abc")
            .build();

        post.like(user);

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
        given(postRepository.findById(anyLong()))
            .willReturn(Optional.of(post));

        // when then
        assertThatThrownBy(() -> postService.like(appUser, postId))
            .isInstanceOf(DuplicatedLikeException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0003")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("이미 좋아요한 게시물 중복 좋아요 에러");

        verify(userRepository, times(1))
            .findByBasicProfile_Name(appUser.getUsername());
        verify(postRepository, times(1))
            .findById(postId);
    }

    @DisplayName("사용자는 좋아요 누르지 않은 게시물을 좋아요 취소 할 수 없다.")
    @Test
    void unlike_UnlikePost_400ExceptionThrown() {
        // given
        AppUser appUser = new LoginUser("test user", "token");
        Long postId = 1L;
        User user = UserFactory.user(1L, appUser.getUsername());
        Post post = new PostBuilder()
            .id(postId)
            .content("abc")
            .build();

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
        given(postRepository.findById(anyLong()))
            .willReturn(Optional.of(post));

        // when then
        assertThatThrownBy(() -> postService.unlike(appUser, postId))
            .isInstanceOf(CannotUnlikeException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0004")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("좋아요 하지 않은 게시물 좋아요 취소 에러");

        verify(userRepository, times(1))
            .findByBasicProfile_Name(appUser.getUsername());
        verify(postRepository, times(1))
            .findById(postId);
    }

    @DisplayName("사용자는 게시물의 태그, 내용을 수정한다.")
    @Test
    void update_TagsAndContentInCaseOfLoginUser_Success() {
        // given
        User user = UserFactory.user(1L, "testUser");
        Post post = new PostBuilder()
            .id(1L)
            .content("testContent")
            .user(user)
            .build();

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
            .tags(List.of("java", "spring"))
            .content("hello")
            .build();
        PostUpdateRequestDto updateRequestDto = PostUpdateRequestDto
            .toUpdateRequestDto(new LoginUser("testUser", "Bearer testToken"), 1L, updateRequest);

        given(postRepository.findById(anyLong()))
            .willReturn(Optional.of(post));
        given(tagService.findOrCreateTags(any(TagsDto.class)))
            .willReturn(List.of(new Tag("java"), new Tag("spring")));

        PostUpdateResponseDto responseDto = PostUpdateResponseDto.builder()
            .tags(List.of("java", "spring"))
            .content("hello")
            .build();

        // when
        PostUpdateResponseDto updateResponseDto = postService.update(updateRequestDto);

        // then
        assertThat(updateResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);

        verify(postRepository, times(1))
            .findById(anyLong());
        verify(tagService, times(1))
            .findOrCreateTags(any(TagsDto.class));
    }

    @DisplayName("사용자는 게시물의 태그를 수정한다.")
    @Test
    void update_TagsInCaseOfLoginUser_Success() {
        // given
        User user = UserFactory.user(1L, "testUser");
        Post post = new PostBuilder()
            .id(1L)
            .content("testContent")
            .user(user)
            .build();

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
            .tags(List.of())
            .content("hello")
            .build();
        PostUpdateRequestDto updateRequestDto = PostUpdateRequestDto
            .toUpdateRequestDto(new LoginUser("testUser", "Bearer testToken"), 1L, updateRequest);

        given(postRepository.findById(anyLong()))
            .willReturn(Optional.of(post));
        given(tagService.findOrCreateTags(any(TagsDto.class)))
            .willReturn(List.of());

        PostUpdateResponseDto responseDto = PostUpdateResponseDto.builder()
            .tags(List.of())
            .content("hello")
            .build();

        // when
        PostUpdateResponseDto updateResponseDto = postService.update(updateRequestDto);

        // then
        assertThat(updateResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);

        verify(postRepository, times(1))
            .findById(anyLong());
        verify(tagService, times(1))
            .findOrCreateTags(any(TagsDto.class));
    }

    @DisplayName("사용자는 게시물의 내용을 수정한다.")
    @Test
    void update_ContentInCaseOfLoginUser_Success() {
        // given
        User user = UserFactory.user(1L, "testUser");
        Post post = new PostBuilder()
            .id(1L)
            .content("testContent")
            .user(user)
            .build();

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
            .tags(List.of("java", "spring"))
            .content("testContent")
            .build();
        PostUpdateRequestDto updateRequestDto = PostUpdateRequestDto
            .toUpdateRequestDto(new LoginUser("testUser", "Bearer testToken"), 1L, updateRequest);

        given(postRepository.findById(anyLong()))
            .willReturn(Optional.of(post));
        given(tagService.findOrCreateTags(any(TagsDto.class)))
            .willReturn(List.of(new Tag("java"), new Tag("spring")));

        PostUpdateResponseDto responseDto = PostUpdateResponseDto.builder()
            .tags(List.of("java", "spring"))
            .content("testContent")
            .build();

        // when
        PostUpdateResponseDto updateResponseDto = postService.update(updateRequestDto);

        // then
        assertThat(updateResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);

        verify(postRepository, times(1))
            .findById(anyLong());
        verify(tagService, times(1))
            .findOrCreateTags(any(TagsDto.class));
    }

    @DisplayName("게스트는 게시물의 내용을 수정할 수 없다.")
    @Test
    void update_GuestUser_401Exception() {
        // given
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
            .tags(List.of("java", "spring"))
            .content("testContent")
            .build();

        // when
        assertThatThrownBy(() -> {
            PostUpdateRequestDto.toUpdateRequestDto(new GuestUser(), 1L, updateRequest);
        }).isInstanceOf(UnauthorizedException.class)
            .hasFieldOrPropertyWithValue("errorCode", "A0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("권한 에러");
    }

    @DisplayName("등록되지 않은 게시물의 내용을 수정할 수 없다. - 500 예외")
    @Test
    void update_InvalidPost_500Exception() {
        // given
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
            .tags(List.of("java", "spring"))
            .content("testContent")
            .build();
        PostUpdateRequestDto updateRequestDto = PostUpdateRequestDto
            .toUpdateRequestDto(new LoginUser("testUser", "Bearer testToken"), 1L, updateRequest);

        given(postRepository.findById(anyLong()))
            .willThrow(new PostNotFoundException(
                "P0002",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "해당하는 게시물을 찾을 수 없습니다."
            ));

        // when
        assertThatThrownBy(() -> {
            postService.update(updateRequestDto);
        }).isInstanceOf(PostNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("해당하는 게시물을 찾을 수 없습니다.");

        // then
        verify(postRepository, times(1))
            .findById(anyLong());
    }

    @DisplayName("사용자는 게시물을 삭제한다.")
    @Test
    void delete_LoginUser_Success() {
        // given
        User user = UserFactory.user(1L, "testUser");
        Post post = new PostBuilder()
            .id(1L)
            .content("testContent")
            .user(user)
            .build();

        given(postRepository.findById(anyLong()))
            .willReturn(Optional.of(post));

        PostDeleteRequestDto deleteRequestDto = PostDeleteRequestDto
            .toPostDeleteRequestDto(new LoginUser("testUser", "Bearer testToken"), 1L);

        // when
        postService.delete(deleteRequestDto);

        // then
        verify(postRepository, times(1))
            .findById(anyLong());
    }

    @DisplayName("게스트는 게시물을 삭제할 수 없다. - 401 예외")
    @Test
    void delete_GuestUser_401Exception() {
        // when
        assertThatThrownBy(() -> {
            PostDeleteRequestDto.toPostDeleteRequestDto(new GuestUser(), 1L);
        }).isInstanceOf(UnauthorizedException.class)
            .hasFieldOrPropertyWithValue("errorCode", "A0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("권한 에러");
    }

    @DisplayName("등록되지 않은 게시물을 삭제할 수 없다. - 500 예외")
    @Test
    void delete_InvalidPost_500Exception() {
        // given
        PostDeleteRequestDto deleteRequestDto = PostDeleteRequestDto
            .toPostDeleteRequestDto(new LoginUser("testUser", "Bearer testToken"), 1L);

        // when
        assertThatThrownBy(() -> {
            postService.delete(deleteRequestDto);
        }).isInstanceOf(PostNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("해당하는 게시물을 찾을 수 없습니다.");

        // then
        verify(postRepository, times(1))
            .findById(anyLong());
    }
}
