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
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.post.CannotUnlikeException;
import com.woowacourse.pickgit.exception.post.DuplicatedLikeException;
import com.woowacourse.pickgit.exception.post.PostFormatException;
import com.woowacourse.pickgit.exception.post.PostNotBelongToUserException;
import com.woowacourse.pickgit.exception.post.RepositoryParseException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.request.PostDeleteRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostUpdateRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchRepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostUpdateResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.content.Image;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.post.domain.repository.PickGitStorage;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositorySearchExtractor;
import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import com.woowacourse.pickgit.post.presentation.dto.request.PostUpdateRequest;
import com.woowacourse.pickgit.tag.application.TagService;
import com.woowacourse.pickgit.tag.application.dto.TagsDto;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.ArrayList;
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
    private PlatformRepositorySearchExtractor platformRepositorySearchExtractor;

    @Mock
    private TagService tagService;

    @DisplayName("사용자는 게시물을 등록할 수 있다.")
    @Test
    void write_LoginUser_Success() {
        // given
        PostRequestDto requestDto = PostFactory.mockPostRequestDtos().get(0);
        User user = UserFactory.user(1L, "testUser");
        Post post = Post.builder()
            .id(1L)
            .content("testContent")
            .images(extractImagesFrom(requestDto))
            .build();

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class)))
            .willReturn(post);
        given(pickGitStorage.storeMultipartFile(anyList(), anyString()))
            .willReturn(extractImageUrlsFrom(requestDto));
        given(tagService.findOrCreateTags(any()))
            .willReturn(extractTagsFrom(requestDto));

        // when
        Long postId = postService.write(requestDto);

        // then
        assertThat(postId).isNotNull();

        verify(userRepository, times(1))
            .findByBasicProfile_Name(requestDto.getUsername());
        verify(postRepository, times(1))
            .save(any(Post.class));
        verify(pickGitStorage, times(1))
            .storeMultipartFile(anyList(), anyString());
        verify(tagService, times(1))
            .findOrCreateTags(any(TagsDto.class));
    }

    private Images extractImagesFrom(PostRequestDto requestDto) {
        List<Image> images = extractImageUrlsFrom(requestDto).stream()
            .map(Image::new)
            .collect(toList());

        return new Images(images);
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
        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(UserFactory.user("kevin")));

        // given
        PostRequestDto requestDto = PostRequestDto.builder()
            .username("kevin")
            .content("a".repeat(501))
            .images(List.of(FileFactory.getTestImage1()))
            .build();

        // when then
        assertThatCode(() -> postService.write(requestDto))
            .isInstanceOf(PostFormatException.class);
    }

    @DisplayName("사용자는 해당하는 페이지에 퍼블릭 레포지토리가 있는 경우 퍼블릭 레포지토리 목록을 가져온다.")
    @Test
    void userRepositories_ValidRepositoriesInCaseOfLoginUser_Success() {
        // given
        String token = "Bearer testToken";
        String username = "testUser";

        RepositoryRequestDto requestDto = createRepositoryRequestDto(token, username, 0L, 50L);
        Pageable pageable = PageRequest.of(0, 50);

        List<RepositoryNameAndUrl> repositories = List.of(
            new RepositoryNameAndUrl("pick", "https://github.com/jipark3/pick"),
            new RepositoryNameAndUrl("git", "https://github.com/jipark3/git")
        );

        given(platformRepositoryExtractor
            .extract(requestDto.getToken(), requestDto.getUsername(), pageable))
            .willReturn(repositories);

        // when
        List<RepositoryResponseDto> repositoryResponseDtos = postService
            .userRepositories(requestDto);

        // then
        assertThat(repositoryResponseDtos).hasSize(2);
        assertThat(repositoryResponseDtos)
            .usingRecursiveComparison()
            .isEqualTo(repositories);

        verify(platformRepositoryExtractor, times(1))
            .extract(requestDto.getToken(), requestDto.getUsername(), pageable);
    }

    @DisplayName("사용자는 해당하는 페이지에 퍼블릭 레포지토리가 없는 경우 빈 배열을 가져온다.")
    @Test
    void userRepositories_EmptyRepositoriesInCaseOfLoginUser_Success() {
        // given
        String token = "Bearer testToken";
        String username = "testUser";

        RepositoryRequestDto requestDto = createRepositoryRequestDto(token, username, 59L, 50L);
        Pageable pageable = PageRequest.of(59, 50);

        List<RepositoryNameAndUrl> repositories = new ArrayList<>();

        given(platformRepositoryExtractor
            .extract(requestDto.getToken(), requestDto.getUsername(), pageable))
            .willReturn(repositories);

        // when
        List<RepositoryResponseDto> repositoryResponseDtos = postService
            .userRepositories(requestDto);

        // then
        assertThat(repositoryResponseDtos).isEmpty();
        assertThat(repositoryResponseDtos)
            .usingRecursiveComparison()
            .isEqualTo(repositories);

        verify(platformRepositoryExtractor, times(1))
            .extract(requestDto.getToken(), requestDto.getUsername(), pageable);
    }

    @DisplayName("유효하지 않은 토큰인 경우 퍼블릭 레포지토리 목록을 가져올 수 없다. - 500 예외")
    @Test
    void userRepositories_InvalidToken_500Exception() {
        // given
        String token = "Bearer invalidToken";
        String username = "testUser";

        RepositoryRequestDto requestDto = createRepositoryRequestDto(token, username, 0L, 50L);
        Pageable pageable = PageRequest.of(0, 50);

        given(platformRepositoryExtractor
            .extract(requestDto.getToken(), requestDto.getUsername(), pageable))
            .willThrow(new RepositoryParseException());

        // when
        assertThatCode(() -> postService.userRepositories(requestDto))
            .isInstanceOf(RepositoryParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("레포지토리 목록을 불러올 수 없습니다.");

        // then
        verify(platformRepositoryExtractor, times(1))
            .extract(requestDto.getToken(), requestDto.getUsername(), pageable);
    }

    @DisplayName("유효하지 않은 유저 이름인 경우 퍼블릭 레포지토리 목록을 가져올 수 없다 - 500 예외")
    @Test
    void userRepositories_InvalidUsername_500Exception() {
        // given
        String token = "Bearer testToken";
        String username = "invalidUser";

        RepositoryRequestDto requestDto = createRepositoryRequestDto(token, username, 0L, 50L);
        Pageable pageable = PageRequest.of(0, 50);

        given(platformRepositoryExtractor
            .extract(requestDto.getToken(), requestDto.getUsername(), pageable))
            .willThrow(new RepositoryParseException());

        // when
        assertThatCode(() -> postService.userRepositories(requestDto))
            .isInstanceOf(RepositoryParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("레포지토리 목록을 불러올 수 없습니다.");

        // then
        verify(platformRepositoryExtractor, times(1))
            .extract(requestDto.getToken(), requestDto.getUsername(), pageable);
    }

    private RepositoryRequestDto createRepositoryRequestDto(String token, String username,
        Long page, Long limit) {
        return RepositoryRequestDto.builder()
            .token(token)
            .username(username)
            .pageable(PageRequest.of(page.intValue(), limit.intValue()))
            .build();
    }

    @DisplayName("사용자는 Repository 목록을 검색해서 가져올 수 있다.")
    @Test
    void searchUserRepositories_LoginUser_Success() {
        // given
        String accessToken = "bearer token";
        String userName = "testUserName";
        String keyword = "pick";
        PageRequest pageable = PageRequest.of(1, 2);

        SearchRepositoryRequestDto searchRepositoryRequestDto = new SearchRepositoryRequestDto(
            accessToken, userName, keyword, pageable
        );

        List<RepositoryNameAndUrl> repositories = List.of(
            new RepositoryNameAndUrl("pick", "https://github.com/jipark3/pick"),
            new RepositoryNameAndUrl("pick-git", "https://github.com/jipark3/pick-git")
        );

        given(platformRepositorySearchExtractor
            .extract(
                searchRepositoryRequestDto.getToken(),
                searchRepositoryRequestDto.getUsername(),
                searchRepositoryRequestDto.getKeyword(),
                pageable
            )
        ).willReturn(repositories);

        // when
        List<RepositoryResponseDto> repositoryResponseDtos = postService
            .searchUserRepositories(searchRepositoryRequestDto);

        // then
        assertThat(repositoryResponseDtos)
            .usingRecursiveComparison()
            .isEqualTo(repositories);
        verify(platformRepositorySearchExtractor, times(1))
            .extract(
                searchRepositoryRequestDto.getToken(),
                searchRepositoryRequestDto.getUsername(),
                searchRepositoryRequestDto.getKeyword(),
                pageable
            );
    }

    @DisplayName("AccessToken이 잘못되었다면, Repository 목록을 검색할 수 없다.")
    @Test
    void searchUserRepositories_InvalidAccessToken_Fail() {
        // given
        String accessToken = "bearer invalid token";
        String userName = "testUserName";
        String keyword = "pick";
        PageRequest pageable = PageRequest.of(1, 2);

        SearchRepositoryRequestDto searchRepositoryRequestDto =
            new SearchRepositoryRequestDto(
                accessToken, userName, keyword, pageable
            );

        given(platformRepositorySearchExtractor
            .extract(
                searchRepositoryRequestDto.getToken(),
                searchRepositoryRequestDto.getUsername(),
                searchRepositoryRequestDto.getKeyword(),
                pageable
            )
        ).willThrow(new RepositoryParseException());

        // when then
        assertThatCode(() ->
            postService.searchUserRepositories(searchRepositoryRequestDto)
        ).isInstanceOf(RepositoryParseException.class);

        verify(platformRepositorySearchExtractor, times(1))
            .extract(
                searchRepositoryRequestDto.getToken(),
                searchRepositoryRequestDto.getUsername(),
                searchRepositoryRequestDto.getKeyword(),
                pageable
            );
    }

    @DisplayName("UserName이 잘못되었다면, Repository 목록을 검색할 수 없다.")
    @Test
    void searchUserRepositories_InvalidUserName_Fail() {
        // given
        String accessToken = "bearer test token";
        String userName = "invalidName";
        String keyword = "pick";
        PageRequest pageable = PageRequest.of(1, 2);

        SearchRepositoryRequestDto searchRepositoryRequestDto = new SearchRepositoryRequestDto(
            accessToken, userName, keyword, pageable
        );

        given(platformRepositorySearchExtractor
            .extract(
                searchRepositoryRequestDto.getToken(),
                searchRepositoryRequestDto.getUsername(),
                searchRepositoryRequestDto.getKeyword(),
                pageable
            )
        ).willThrow(new RepositoryParseException());

        // when then
        assertThatCode(() ->
            postService.searchUserRepositories(searchRepositoryRequestDto)
        ).isInstanceOf(RepositoryParseException.class);

        verify(platformRepositorySearchExtractor, times(1))
            .extract(
                searchRepositoryRequestDto.getToken(),
                searchRepositoryRequestDto.getUsername(),
                searchRepositoryRequestDto.getKeyword(),
                pageable
            );
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
                Post.builder()
                    .id(postId)
                    .content("abc")
                    .build())
            );

        // when
        LikeResponseDto likeResponseDto = postService.like(appUser, postId);

        // then
        assertThat(likeResponseDto.getLikesCount()).isEqualTo(1);
        assertThat(likeResponseDto.getLiked()).isTrue();

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
        Post post = Post.builder()
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
        assertThat(likeResponseDto.getLikesCount()).isEqualTo(0);
        assertThat(likeResponseDto.getLiked()).isFalse();

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
        Post post = Post.builder()
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
        Post post = Post.builder()
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
        LoginUser loginUser = new LoginUser("testUser", "Bearer testToken");
        User user = UserFactory.user(1L, loginUser.getUsername());
        Post post = Post.builder()
            .id(1L)
            .content("testContent")
            .author(user)
            .build();

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
            .tags(List.of("java", "spring"))
            .content("hello")
            .build();
        PostUpdateRequestDto updateRequestDto = new PostUpdateRequestDto(loginUser, 1L,
            updateRequest.getTags(), updateRequest.getContent());

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
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

        verify(userRepository, times(1))
            .findByBasicProfile_Name("testUser");
        verify(postRepository, times(1))
            .findById(1L);
        verify(tagService, times(1))
            .findOrCreateTags(any(TagsDto.class));
    }

    @DisplayName("사용자는 게시물의 태그를 수정한다.")
    @Test
    void update_TagsInCaseOfLoginUser_Success() {
        // given
        LoginUser loginUser = new LoginUser("testUser", "Bearer testToken");
        User user = UserFactory.user(1L, loginUser.getUsername());
        Post post = Post.builder()
            .id(1L)
            .content("testContent")
            .author(user)
            .build();

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
            .tags(List.of())
            .content("hello")
            .build();
        PostUpdateRequestDto updateRequestDto = new PostUpdateRequestDto(loginUser, 1L,
            updateRequest.getTags(), updateRequest.getContent());

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
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

        verify(userRepository, times(1))
            .findByBasicProfile_Name("testUser");
        verify(postRepository, times(1))
            .findById(1L);
        verify(tagService, times(1))
            .findOrCreateTags(any(TagsDto.class));
    }

    @DisplayName("사용자는 게시물의 내용을 수정한다.")
    @Test
    void update_ContentInCaseOfLoginUser_Success() {
        // given
        LoginUser loginUser = new LoginUser("testUser", "Bearer testToken");
        User user = UserFactory.user(1L, loginUser.getUsername());
        Post post = Post.builder()
            .id(1L)
            .content("testContent")
            .author(user)
            .build();

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
            .tags(List.of("java", "spring"))
            .content("testContent")
            .build();
        PostUpdateRequestDto updateRequestDto = new PostUpdateRequestDto(loginUser, 1L,
            updateRequest.getTags(), updateRequest.getContent());

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
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

        verify(userRepository, times(1))
            .findByBasicProfile_Name("testUser");
        verify(postRepository, times(1))
            .findById(1L);
        verify(tagService, times(1))
            .findOrCreateTags(any(TagsDto.class));
    }

    @DisplayName("게스트는 게시물의 내용을 수정할 수 없다.")
    @Test
    void update_GuestUser_401Exception() {
        // given
        GuestUser guestUser = new GuestUser();

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
            .tags(List.of("java", "spring"))
            .content("testContent")
            .build();

        // when
        assertThatThrownBy(() -> {
            new PostUpdateRequestDto(guestUser, 1L,
                updateRequest.getTags(), updateRequest.getContent());
        }).isInstanceOf(UnauthorizedException.class)
            .hasFieldOrPropertyWithValue("errorCode", "A0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("권한 에러");
    }

    @DisplayName("해당하는 사용자의 게시물이 아닌 경우 수정할 수 없다. - 401 예외")
    @Test
    void update_PostNotBelongToUser_401Exception() {
        // given
        LoginUser loginUser = new LoginUser("testUser", "Bearer testToken");
        User user = UserFactory.user(1L, loginUser.getUsername());

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
        given(postRepository.findById(anyLong()))
            .willThrow(new PostNotBelongToUserException());

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
            .tags(List.of("java", "spring"))
            .content("testContent")
            .build();
        PostUpdateRequestDto updateRequestDto = new PostUpdateRequestDto(loginUser, 1L,
            updateRequest.getTags(), updateRequest.getContent());

        // when
        assertThatThrownBy(() -> {
            postService.update(updateRequestDto);
        }).isInstanceOf(PostNotBelongToUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0005")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("해당하는 사용자의 게시물이 아닙니다.");

        // then
        verify(userRepository, times(1))
            .findByBasicProfile_Name("testUser");
        verify(postRepository, times(1))
            .findById(1L);
    }

    @DisplayName("사용자는 게시물을 삭제한다.")
    @Test
    void delete_LoginUser_Success() {
        // given
        LoginUser loginUser = new LoginUser("testUser", "Bearer testToken");
        User user = UserFactory.user(1L, loginUser.getUsername());
        Post post = Post.builder()
            .id(1L)
            .content("testContent")
            .author(user)
            .build();

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
        given(postRepository.findById(anyLong()))
            .willReturn(Optional.of(post));
        willDoNothing()
            .given(postRepository)
            .delete(any(Post.class));

        PostDeleteRequestDto deleteRequestDto = new PostDeleteRequestDto(loginUser, 1L);

        // when
        postService.delete(deleteRequestDto);

        // then
        verify(userRepository, times(1))
            .findByBasicProfile_Name("testUser");
        verify(postRepository, times(1))
            .findById(1L);
        verify(postRepository, times(1))
            .delete(any(Post.class));
    }

    @DisplayName("게스트는 게시물을 삭제할 수 없다. - 401 예외")
    @Test
    void delete_GuestUser_401Exception() {
        // given
        GuestUser guestUser = new GuestUser();

        // when
        assertThatThrownBy(() -> {
            new PostDeleteRequestDto(guestUser, 1L);
        }).isInstanceOf(UnauthorizedException.class)
            .hasFieldOrPropertyWithValue("errorCode", "A0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("권한 에러");
    }

    @DisplayName("해당하는 사용자의 게시물이 아닌 경우 삭제할 수 없다. - 401 예외")
    @Test
    void delete_PostNotBelongToUser_401Exception() {
        // given
        LoginUser loginUser = new LoginUser("testUser", "Bearer testToken");
        User user = UserFactory.user(1L, loginUser.getUsername());

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
        given(postRepository.findById(anyLong()))
            .willThrow(new PostNotBelongToUserException());

        PostDeleteRequestDto deleteRequestDto = new PostDeleteRequestDto(loginUser, 1L);

        // when
        assertThatThrownBy(() -> {
            postService.delete(deleteRequestDto);
        }).isInstanceOf(PostNotBelongToUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0005")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("해당하는 사용자의 게시물이 아닙니다.");

        // then
        verify(userRepository, times(1))
            .findByBasicProfile_Name("testUser");
        verify(postRepository, times(1))
            .findById(1L);
    }
}
