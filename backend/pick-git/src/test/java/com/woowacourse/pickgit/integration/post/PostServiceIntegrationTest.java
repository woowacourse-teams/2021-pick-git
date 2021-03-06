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

    @DisplayName("???????????? ???????????? ????????? ??? ??????.")
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

    @DisplayName("???????????? ????????? ????????? ????????? ????????? ????????? ??? ??????.")
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

    @DisplayName("???????????? Repository ????????? ????????? ??? ??????.")
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

    @DisplayName("????????? ???????????? ?????? ?????? ????????? ????????????. - 500 ??????")
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

    @DisplayName("???????????? ???????????? ?????? ?????? ????????? ????????????. - 500 ??????")
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

    @DisplayName("???????????? Repository ????????? ???????????? ????????? ??? ??????.")
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

    @DisplayName("??????????????? ?????? ??? ????????? ???????????? ?????? ?????? ????????? ????????????. - 500 ??????")
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

    @DisplayName("???????????? ?????? ???????????? ????????? ??? ??? ??????. - ??????")
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

    @DisplayName("???????????? ?????? ???????????? ????????? ?????? ??? ??? ??????. - ??????")
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

    @DisplayName("???????????? ?????? ????????? ??? ???????????? ????????? ?????? ??? ??? ??????. - ??????")
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
            .hasMessage("?????? ???????????? ????????? ?????? ????????? ??????");
    }

    @DisplayName("???????????? ???????????? ????????? ??? ??? ??????. - ??????")
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
            .hasMessage("?????? ??????");
    }

    @DisplayName("???????????? ????????? ????????? ?????? ???????????? ????????? ?????? ??? ??? ??????. - ??????")
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
            .hasMessage("????????? ?????? ?????? ????????? ????????? ?????? ??????");
    }

    @DisplayName("???????????? ???????????? ????????? ?????? ??? ??? ??????. - ??????")
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
            .hasMessage("?????? ??????");
    }

    @DisplayName("???????????? ???????????? ??????, ????????? ????????????.")
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

    @DisplayName("???????????? ???????????? ????????? ????????????.")
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

    @DisplayName("???????????? ???????????? ????????? ????????????.")
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

    @DisplayName("???????????? ???????????? ???????????? ?????? ?????? ????????? ??? ??????. - 401 ??????")
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
            .hasMessage("???????????? ???????????? ???????????? ????????????.");
    }

    @DisplayName("???????????? ???????????? ????????? ???????????? ????????? ??? ??????. - 400 ??????")
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
            .hasMessage("?????? ?????? ??????");
    }

    @DisplayName("???????????? ????????? ?????? ???????????? ????????????.")
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

    @DisplayName("???????????? ????????? ?????? ???????????? ????????????.")
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

    @DisplayName("???????????? ???????????? ???????????? ?????? ?????? ????????? ??? ??????. - 401 ??????")
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
            .hasMessage("???????????? ???????????? ???????????? ????????????.");
    }
}
