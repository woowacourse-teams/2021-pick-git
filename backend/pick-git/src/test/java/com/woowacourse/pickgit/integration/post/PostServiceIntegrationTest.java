package com.woowacourse.pickgit.integration.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.exception.post.CannotAddTagException;
import com.woowacourse.pickgit.exception.post.CannotUnlikeException;
import com.woowacourse.pickgit.exception.post.DuplicatedLikeException;
import com.woowacourse.pickgit.exception.post.PostNotBelongToUserException;
import com.woowacourse.pickgit.integration.IntegrationTest;
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
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;


class PostServiceIntegrationTest extends IntegrationTest {

    private static final String USERNAME = "jipark3";
    private static final String ACCESS_TOKEN = "oauth.access.token";

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentService commentService;

    @DisplayName("사용자는 게시물을 등록할 수 있다.")
    @Test
    void write_LoginUser_Success() {
        // given
        User user = UserFactory.user(USERNAME);
        userRepository.save(user);

        PostRequestDto postRequestDto = PostRequestDto.builder()
            .token(ACCESS_TOKEN)
            .username(USERNAME)
            .images(List.of(
                FileFactory.getTestImage1(),
                FileFactory.getTestImage2()
            ))
            .githubRepoUrl("https://github.com/bperhaps")
            .tags(List.of("java", "c++"))
            .content("testContent")
            .build();

        // when
        Long postId = postService.write(postRequestDto);

        // then
        assertThat(postId).isNotNull();
    }

    @DisplayName("사용자는 게시글 등록시 중복된 태그를 작성할 수 없다.")
    @Test
    void write_LoginUserWithDuplicateTag_Fail() {
        // given
        userRepository.save(UserFactory.user(USERNAME));

        PostRequestDto requestDto = PostRequestDto.builder()
            .token(ACCESS_TOKEN)
            .username(USERNAME)
            .images(
                List.of(
                    FileFactory.getTestImage1(),
                    FileFactory.getTestImage2()
                )
            )
            .githubRepoUrl("https://github.com/bperhaps")
            .tags(List.of("Java", "Javascript", "Java"))
            .content("content").build();

        // when, then
        assertThatCode(() -> postService.write(requestDto))
            .isInstanceOf(CannotAddTagException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
    }

    @DisplayName("사용자는 Repository 목록을 가져올 수 있다.")
    @Test
    void showRepositories_LoginUser_Success() {
        // given
        RepositoryRequestDto requestDto = createRepositoryRequestDto(ACCESS_TOKEN, USERNAME);

        // when
        List<RepositoryResponseDto> repositoryResponseDtos = postService
            .userRepositories(requestDto);

        // then
        assertThat(repositoryResponseDtos).hasSize(2);
    }

    @DisplayName("토큰이 유효하지 않은 경우 예외가 발생한다. - 500 예외")
    @Test
    void showRepositories_InvalidAccessToken_500Exception() {
        // given
        RepositoryRequestDto requestDto = createRepositoryRequestDto("invalidToken", USERNAME);

        // then
        assertThatThrownBy(() -> {
            postService.userRepositories(requestDto);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
    }

    @DisplayName("사용자가 유효하지 않은 경우 예외가 발생한다. - 500 예외")
    @Test
    void showRepositories_InvalidUsername_404Exception() {
        // given
        RepositoryRequestDto requestDto = createRepositoryRequestDto(ACCESS_TOKEN, "invalidUser");

        // then
        assertThatThrownBy(() ->
            postService.userRepositories(requestDto)
        ).isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
    }

    private RepositoryRequestDto createRepositoryRequestDto(String token, String username) {
        return RepositoryRequestDto.builder()
            .token(token)
            .username(username)
            .pageable(PageRequest.of(0, 50))
            .build();
    }

    @DisplayName("사용자는 Repository 목록을 검색해서 가져올 수 있다.")
    @Test
    void searchUserRepositories_LoginUser_Success() {
        // given
        SearchRepositoryRequestDto requestDto =
            new SearchRepositoryRequestDto(
                ACCESS_TOKEN,
                USERNAME,
                "woowa",
                PageRequest.of(0, 2));

        // when
        List<RepositoryResponseDto> repositoryResponseDtos = postService
            .searchUserRepositories(requestDto);

        // then
        assertThat(repositoryResponseDtos).hasSize(2);
    }

    @DisplayName("레포지토리 검색 시 토큰이 유효하지 않은 경우 예외가 발생한다. - 500 예외")
    @Test
    void searchUserRepositories_InvalidAccessToken_500Exception() {
        // given
        String invalidToken = "invalidToken";

        SearchRepositoryRequestDto requestDto = new SearchRepositoryRequestDto(
            invalidToken,
            USERNAME,
            "woowa",
            PageRequest.of(0, 2)
        );

        // then
        assertThatThrownBy(() -> {
            postService.searchUserRepositories(requestDto);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
    }

    @DisplayName("사용자는 특정 게시물을 좋아요 할 수 있다. - 성공")
    @Test
    void like_ValidUser_Success() {
        // given
        PostRequestDto postRequestDtos = PostFactory.mockPostRequestDtos().get(0);
        User loginUser = userRepository.save(UserFactory.user(postRequestDtos.getUsername()));

        AppUser appUser = new LoginUser(loginUser.getName(), "token");

        Long postId = postService.write(postRequestDtos);

        // when
        LikeResponseDto likeResponseDto = postService.like(appUser, postId);

        // then
        assertThat(likeResponseDto.getLikesCount()).isEqualTo(1);
        assertThat(likeResponseDto.getLiked()).isTrue();
    }

    @DisplayName("사용자는 특정 게시물을 좋아요 취소 할 수 있다. - 성공")
    @Test
    void unlike_ValidUser_Success() {
        // given
        PostRequestDto postRequestDtos = PostFactory.mockPostRequestDtos().get(0);
        User loginUser = userRepository.save(UserFactory.user(postRequestDtos.getUsername()));

        AppUser appUser = new LoginUser(loginUser.getName(), "token");

        Long postId = postService.write(postRequestDtos);
        postService.like(appUser, postId);

        // when
        LikeResponseDto likeResponseDto = postService.unlike(appUser, postId);

        // then
        assertThat(likeResponseDto.getLikesCount()).isEqualTo(0);
        assertThat(likeResponseDto.getLiked()).isFalse();
    }

    @DisplayName("사용자는 이미 좋아요 한 게시물을 좋아요 추가 할 수 없다. - 실패")
    @Test
    void like_DuplicatedLike_400ExceptionThrown() {
        // given
        PostRequestDto postRequestDtos = PostFactory.mockPostRequestDtos().get(0);
        User loginUser = userRepository.save(UserFactory.user(postRequestDtos.getUsername()));

        AppUser appUser = new LoginUser(loginUser.getName(), "token");

        Long postId = postService.write(postRequestDtos);
        postService.like(appUser, postId);

        // when then
        assertThatThrownBy(() -> postService.like(appUser, postId))
            .isInstanceOf(DuplicatedLikeException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0003")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("이미 좋아요한 게시물 중복 좋아요 에러");
    }

    @DisplayName("게스트는 게시물을 좋아요 할 수 없다. - 실패")
    @Test
    void like_GuestUser_401ExceptionThrown() {
        // given
        PostRequestDto postRequestDtos = PostFactory.mockPostRequestDtos().get(0);
        userRepository.save(UserFactory.user(postRequestDtos.getUsername()));

        AppUser appUser = new GuestUser();

        Long postId = postService.write(postRequestDtos);

        // when then
        assertThatThrownBy(() -> postService.like(appUser, postId))
            .isInstanceOf(UnauthorizedException.class)
            .hasFieldOrPropertyWithValue("errorCode", "A0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("권한 에러");
    }

    @DisplayName("사용자는 좋아요 누르지 않은 게시물을 좋아요 취소 할 수 없다. - 실패")
    @Test
    void unlike_UnlikePost_400ExceptionThrown() {
        // given
        PostRequestDto postRequestDtos = PostFactory.mockPostRequestDtos().get(0);
        User loginUser = userRepository.save(UserFactory.user(postRequestDtos.getUsername()));

        AppUser appUser = new LoginUser(loginUser.getName(), "token");

        Long postId = postService.write(postRequestDtos);

        // when then
        assertThatThrownBy(() -> postService.unlike(appUser, postId))
            .isInstanceOf(CannotUnlikeException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0004")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("좋아요 하지 않은 게시물 좋아요 취소 에러");
    }

    @DisplayName("게스트는 게시물을 좋아요 취소 할 수 없다. - 실패")
    @Test
    void unlike_GuestUser_401ExceptionThrown() {
        // given
        PostRequestDto postRequestDtos = PostFactory.mockPostRequestDtos().get(0);
        userRepository.save(UserFactory.user(postRequestDtos.getUsername()));
        User likeUser = userRepository.save(UserFactory.user());

        AppUser loginUser = new LoginUser(likeUser.getName(), "token");
        AppUser guestUser = new GuestUser();

        Long postId = postService.write(postRequestDtos);
        postService.like(loginUser, postId);

        // when then
        assertThatThrownBy(() -> postService.unlike(guestUser, postId))
            .isInstanceOf(UnauthorizedException.class)
            .hasFieldOrPropertyWithValue("errorCode", "A0002")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("권한 에러");
    }

    @DisplayName("사용자는 게시물의 태그, 내용을 수정한다.")
    @Test
    void update_TagsAndContentInCaseOfLoginUser_Success() {
        // given
        User user = UserFactory.user(USERNAME);
        userRepository.save(user);
        LoginUser loginUser = new LoginUser(USERNAME, ACCESS_TOKEN);

        PostRequestDto requestDto = PostRequestDto.builder()
            .token(ACCESS_TOKEN)
            .username(USERNAME)
            .images(List.of(
                FileFactory.getTestImage1(),
                FileFactory.getTestImage2()))
            .githubRepoUrl("https://github.com/da-nyee/woowacourse-projects")
            .tags(List.of("java", "spring"))
            .content("testContent")
            .build();

        postService.write(requestDto);

        PostUpdateRequestDto updateRequestDto = new PostUpdateRequestDto(loginUser, 1L,
            List.of("java", "spring", "spring-boot"), "hello");

        PostUpdateResponseDto responseDto = new PostUpdateResponseDto(
            List.of("java", "spring", "spring-boot"), "hello");

        // when
        PostUpdateResponseDto updateResponseDto = postService.update(updateRequestDto);

        // then
        assertThat(updateResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("사용자는 게시물의 태그를 수정한다.")
    @Test
    void update_TagsInCaseOfLoginUser_Success() {
        // given
        User user = UserFactory.user(USERNAME);
        userRepository.save(user);
        LoginUser loginUser = new LoginUser(USERNAME, ACCESS_TOKEN);

        PostRequestDto requestDto = PostRequestDto.builder()
            .token(ACCESS_TOKEN)
            .username(USERNAME)
            .images(List.of(
                FileFactory.getTestImage1(),
                FileFactory.getTestImage2()))
            .githubRepoUrl("https://github.com/da-nyee/woowacourse-projects")
            .tags(List.of("java", "spring"))
            .content("testContent")
            .build();

        postService.write(requestDto);

        PostUpdateRequestDto updateRequestDto = new PostUpdateRequestDto(loginUser, 1L,
            List.of("java", "spring", "spring-boot"), "testContent");

        PostUpdateResponseDto responseDto = new PostUpdateResponseDto(
            List.of("java", "spring", "spring-boot"), "testContent");

        // when
        PostUpdateResponseDto updateResponseDto = postService.update(updateRequestDto);

        // then
        assertThat(updateResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("사용자는 게시물의 내용을 수정한다.")
    @Test
    void update_ContentInCaseOfLoginUser_Success() {
        // given
        User user = UserFactory.user(USERNAME);
        userRepository.save(user);
        LoginUser loginUser = new LoginUser(USERNAME, ACCESS_TOKEN);

        PostRequestDto requestDto = PostRequestDto.builder()
            .token(ACCESS_TOKEN)
            .username(USERNAME)
            .images(List.of(
                FileFactory.getTestImage1(),
                FileFactory.getTestImage2()))
            .githubRepoUrl("https://github.com/da-nyee/woowacourse-projects")
            .tags(List.of("java", "spring"))
            .content("testContent")
            .build();

        postService.write(requestDto);

        PostUpdateRequestDto updateRequestDto = new PostUpdateRequestDto(loginUser, 1L,
            List.of("java", "spring"), "hello");

        PostUpdateResponseDto responseDto = new PostUpdateResponseDto(
            List.of("java", "spring"), "hello");

        // when
        PostUpdateResponseDto updateResponseDto = postService.update(updateRequestDto);

        // then
        assertThat(updateResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(responseDto);
    }

    @DisplayName("해당하는 사용자의 게시물이 아닌 경우 수정할 수 없다. - 401 예외")
    @Test
    void update_PostNotBelongToUser_401Exception() {
        // given
        User user = UserFactory.user(USERNAME);
        User anotherUser = UserFactory.user("anotherUser");
        userRepository.save(user);
        userRepository.save(anotherUser);
        LoginUser loginUser = new LoginUser(USERNAME, ACCESS_TOKEN);

        PostRequestDto requestDto = PostRequestDto.builder()
            .token(ACCESS_TOKEN)
            .username("anotherUser")
            .images(List.of(
                FileFactory.getTestImage1(),
                FileFactory.getTestImage2()))
            .githubRepoUrl("https://github.com/da-nyee/woowacourse-projects")
            .tags(List.of("java", "spring"))
            .content("testContent")
            .build();

        postService.write(requestDto);

        PostUpdateRequestDto updateRequestDto = new PostUpdateRequestDto(loginUser, 1L,
            List.of("java", "spring"), "hello");

        // when
        assertThatThrownBy(() ->
            postService.update(updateRequestDto)
        ).isInstanceOf(PostNotBelongToUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0005")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("해당하는 사용자의 게시물이 아닙니다.");
    }

    @DisplayName("사용자는 중복되는 태그로 게시물을 수정할 수 없다. - 400 예외")
    @Test
    void update_DuplicateTags_400Exception() {
        // given
        User user = UserFactory.user(USERNAME);
        userRepository.save(user);
        LoginUser loginUser = new LoginUser(USERNAME, ACCESS_TOKEN);

        PostRequestDto requestDto = PostRequestDto.builder()
            .token(ACCESS_TOKEN)
            .username(USERNAME)
            .images(List.of(
                FileFactory.getTestImage1(),
                FileFactory.getTestImage2()))
            .githubRepoUrl("https://github.com/da-nyee/woowacourse-projects")
            .tags(List.of("java", "spring"))
            .content("testContent")
            .build();

        postService.write(requestDto);

        PostUpdateRequestDto updateRequestDto = new PostUpdateRequestDto(loginUser, 1L,
            List.of("java", "java"), "testContent");

        // when
        assertThatThrownBy(() -> {
            postService.update(updateRequestDto);
        }).isInstanceOf(CannotAddTagException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("태그 추가 에러");
    }

    @DisplayName("사용자는 댓글이 없는 게시물을 삭제한다.")
    @Test
    void delete_PostWithNoCommentInCaseOfLoginUser_Success() {
        // given
        User user = UserFactory.user(USERNAME);
        User savedUser = userRepository.save(user);
        LoginUser loginUser = new LoginUser(savedUser.getName(), ACCESS_TOKEN);

        Post post = Post.builder()
            .content("testContent")
            .githubRepoUrl("https://github.com/da-nyee")
            .author(savedUser)
            .build();

        Post savedPost = postRepository.save(post);

        PostDeleteRequestDto deleteRequestDto = new PostDeleteRequestDto(loginUser,
            savedPost.getId());

        // when
        postService.delete(deleteRequestDto);

        // then
        assertThat(postRepository.findById(savedPost.getId())).isEmpty();
    }

    @DisplayName("사용자는 댓글이 있는 게시물을 삭제한다.")
    @Test
    void delete_PostWithCommentInCaseOfLoginUser_Success() {
        // given
        User user = UserFactory.user(USERNAME);
        User savedUser = userRepository.save(user);
        LoginUser loginUser = new LoginUser(savedUser.getName(), ACCESS_TOKEN);

        User kevin = UserFactory.user("kevin");
        User savedKevin = userRepository.save(kevin);

        Post post = Post.builder()
            .content("testContent")
            .githubRepoUrl("https://github.com/da-nyee")
            .author(savedUser)
            .build();

        Post savedPost = postRepository.save(post);

        CommentRequestDto request = CommentRequestDto.builder()
            .userName(savedKevin.getName())
            .content("testComment")
            .postId(savedPost.getId())
            .build();

        commentService.addComment(request);

        PostDeleteRequestDto deleteRequestDto = new PostDeleteRequestDto(loginUser,
            savedPost.getId());

        // when
        postService.delete(deleteRequestDto);

        // then
        assertThat(postRepository.findById(savedPost.getId())).isEmpty();
    }

    @DisplayName("해당하는 사용자의 게시물이 아닌 경우 삭제할 수 없다. - 401 예외")
    @Test
    void delete_PostNotBelongToUser_401Exception() {
        // given
        User user = UserFactory.user(USERNAME);
        User kevin = UserFactory.user("kevin");
        userRepository.save(user);
        User savedKevin = userRepository.save(kevin);
        LoginUser loginUser = new LoginUser(USERNAME, ACCESS_TOKEN);

        Post post = Post.builder()
            .content("testContent")
            .githubRepoUrl("https://github.com/da-nyee")
            .author(savedKevin)
            .build();

        Post savedPost = postRepository.save(post);

        PostDeleteRequestDto deleteRequestDto = new PostDeleteRequestDto(loginUser,
            savedPost.getId());

        // when
        assertThatThrownBy(() ->
            postService.delete(deleteRequestDto)
        ).isInstanceOf(PostNotBelongToUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0005")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("해당하는 사용자의 게시물이 아닙니다.");
    }
}
