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
import com.woowacourse.pickgit.exception.post.CommentFormatException;
import com.woowacourse.pickgit.exception.post.DuplicatedLikeException;
import com.woowacourse.pickgit.exception.post.PostFormatException;
import com.woowacourse.pickgit.exception.post.PostNotBelongToUserException;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.exception.post.RepositoryParseException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostDeleteRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostUpdateRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostImageUrlResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostUpdateResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponseDtos;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.content.Image;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.post.domain.repository.PickGitStorage;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.domain.util.dto.RepositoryUrlAndName;
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
        PostImageUrlResponseDto responseDto = postService.write(requestDto);

        // then
        assertThat(responseDto.getId()).isNotNull();
        assertThat(responseDto.getImageUrls()).containsAll(extractImageUrlsFrom(requestDto));

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

    @DisplayName("게시물에 댓글을 정상 등록한다.")
    @Test
    void addComment_ValidContent_Success() {
        //given
        String comment_content = "test comment";
        User user = UserFactory.user(1L, "testUser1");
        Post post = Post.builder()
            .id(1L)
            .author(user)
            .build();

        given(postRepository.findById(anyLong()))
            .willReturn(Optional.of(post));
        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));

        CommentRequestDto commentRequestDto =
            new CommentRequestDto(user.getName(), comment_content, post.getId());

        //when
        CommentResponseDto commentResponseDto = postService.addComment(commentRequestDto);

        //then
        assertThat(commentResponseDto.getAuthorName()).isEqualTo(user.getName());
        assertThat(commentResponseDto.getContent()).isEqualTo(comment_content);

        verify(postRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findByBasicProfile_Name(anyString());
    }

    @DisplayName("게시물에 빈 댓글을 등록할 수 없다.")
    @Test
    void addComment_InvalidContent_ExceptionThrown() {
        Post post = Post.builder()
            .id(1L)
            .build();

        User user = UserFactory.user("testuser");

        CommentRequestDto commentRequestDto =
            new CommentRequestDto(user.getName(), "", post.getId());

        // then
        assertThatCode(() -> postService.addComment(commentRequestDto))
            .isInstanceOf(CommentFormatException.class)
            .extracting("errorCode")
            .isEqualTo("F0002");
    }

    @DisplayName("존재하지 않는 사용자는 댓글을 등록할 수 없다.")
    @Test
    void addComment_invalidUser_ExceptionOccur() {
        //given
        CommentRequestDto commentRequestDto =
            new CommentRequestDto("invalidUser", "comment_content", 1L);

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willThrow(new UserNotFoundException());

        //when then
        assertThatCode(() -> postService.addComment(commentRequestDto))
            .isInstanceOf(UserNotFoundException.class)
            .extracting("errorCode")
            .isEqualTo("U0001");

        verify(userRepository, times(1)).findByBasicProfile_Name(anyString());
    }

    @DisplayName("존재하지 않는 게시물에는 댓글을 등록할 수 없다.")
    @Test
    void addComment_invalidPost_ExceptionOccur() {
        //given
        CommentRequestDto commentRequestDto =
            new CommentRequestDto("testUser", "comment_content", 1L);

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(UserFactory.user()));
        given(postRepository.findById(anyLong()))
            .willThrow(new PostNotFoundException());

        //when then
        assertThatCode(() -> postService.addComment(commentRequestDto))
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
        List<RepositoryUrlAndName> repositories = List.of(
            new RepositoryUrlAndName("pick", "https://github.com/jipark3/pick"),
            new RepositoryUrlAndName("git", "https://github.com/jipark3/git")
        );

        given(platformRepositoryExtractor
            .extract(requestDto.getToken(), requestDto.getUsername()))
            .willReturn(repositories);

        // when
        RepositoryResponseDtos repositoryResponseDtos = postService.userRepositories(requestDto);
        List<RepositoryResponseDto> responseDtos = repositoryResponseDtos.getRepositoryResponseDtos();

        // then
        assertThat(responseDtos)
            .usingRecursiveComparison()
            .isEqualTo(repositories);
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
            .willThrow(new RepositoryParseException());

        // when then
        assertThatCode(() -> postService.userRepositories(requestDto))
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
        assertThatCode(() -> postService.userRepositories(requestDto))
            .isInstanceOf(RepositoryParseException.class);

        verify(platformRepositoryExtractor, times(1))
            .extract(requestDto.getToken(), requestDto.getUsername());
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
