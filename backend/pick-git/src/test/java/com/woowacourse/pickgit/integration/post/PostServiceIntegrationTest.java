package com.woowacourse.pickgit.integration.post;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.PostBuilder;
import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.exception.post.CannotAddTagException;
import com.woowacourse.pickgit.exception.post.CannotUnlikeException;
import com.woowacourse.pickgit.exception.post.CommentFormatException;
import com.woowacourse.pickgit.exception.post.DuplicatedLikeException;
import com.woowacourse.pickgit.exception.post.PostNotBelongToUserException;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
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
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.post.presentation.dto.request.CommentRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.HomeFeedRequest;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@Import(InfrastructureTestConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class PostServiceIntegrationTest {

    private static final String USERNAME = "jipark3";
    private static final String ACCESS_TOKEN = "oauth.access.token";

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("게시물에 댓글을 정상 등록한다.")
    @Test
    void addComment_ValidContent_Success() {
        User testUser = UserFactory.user("testUser");
        User savedTestUser = userRepository.save(testUser);

        User kevin = UserFactory.user("kevin");
        User savedKevin = userRepository.save(kevin);

        Post post = new PostBuilder()
            .content("testContent")
            .githubRepoUrl("https://github.com/bperhaps")
            .user(savedTestUser)
            .build();
        Post savedPost = postRepository.save(post);

        // when
        CommentRequest commentRequest = CommentRequest.builder()
            .userName("kevin")
            .content("test comment")
            .postId(savedPost.getId())
            .build();

        CommentResponse commentResponseDto = postService.addComment(commentRequest);

        // then
        assertThat(commentResponseDto.getAuthorName()).isEqualTo(savedKevin.getName());
        assertThat(commentResponseDto.getContent()).isEqualTo("test comment");
    }

    @DisplayName("Post에 빈 Comment은 등록할 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void addComment_InvalidContent_ExceptionThrown(String content) {
        User testUser = UserFactory.user("testUser");
        User savedTestUser = userRepository.save(testUser);

        User kevin = UserFactory.user("kevin");
        User savedKevin = userRepository.save(kevin);

        Post post = new PostBuilder()
            .content("testContent")
            .githubRepoUrl("https://github.com/bperhaps")
            .user(savedTestUser)
            .build();
        Post savedPost = postRepository.save(post);

        CommentRequest commentRequest = CommentRequest.builder()
            .userName("kevin")
            .content(content)
            .postId(savedPost.getId())
            .build();

        // then
        assertThatCode(() -> postService.addComment(commentRequest))
            .isInstanceOf(CommentFormatException.class)
            .extracting("errorCode")
            .isEqualTo("F0002");
    }

    @DisplayName("존재하지 않는 Post에 Comment를 등록할 수 없다.")
    @Test
    void addComment_PostNotFound_ExceptionThrown() {
        userRepository.save(UserFactory.user("kevin"));

        // when
        CommentRequest commentRequest = CommentRequest.builder()
            .userName("kevin")
            .content("content")
            .postId(-1L)
            .build();

        // then
        assertThatCode(() -> postService.addComment(commentRequest))
            .isInstanceOf(PostNotFoundException.class)
            .extracting("errorCode")
            .isEqualTo("P0002");
    }

    @DisplayName("존재하지 않는 User는 Comment를 등록할 수 없다.")
    @Test
    void addComment_UserNotFound_ExceptionThrown() {
        // given
        Post post = new PostBuilder()
            .build();
        Post savedPost = postRepository.save(post);

        // when
        CommentRequest commentRequest = CommentRequest.builder()
            .userName("anonymous")
            .content("content")
            .postId(savedPost.getId())
            .build();

        // then
        assertThatCode(() -> postService.addComment(commentRequest))
            .isInstanceOf(UserNotFoundException.class)
            .extracting("errorCode")
            .isEqualTo("U0001");
    }

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
        PostImageUrlResponseDto responseDto = postService.write(postRequestDto);

        // then
        assertThat(responseDto.getId()).isNotNull();
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
        RepositoryRequestDto requestDto = new RepositoryRequestDto(ACCESS_TOKEN, USERNAME);

        // when
        RepositoriesResponseDto responseDto = postService.showRepositories(requestDto);

        // then
        assertThat(responseDto.getRepositories()).hasSize(2);
    }

    @DisplayName("토큰이 유효하지 않은 경우 예외가 발생한다. - 500 예외")
    @Test
    void showRepositories_InvalidAccessToken_401Exception() {
        // given
        String invalidToken = "invalidToken";

        RepositoryRequestDto requestDto =
            new RepositoryRequestDto(invalidToken, USERNAME);

        // then
        assertThatThrownBy(() -> {
            postService.showRepositories(requestDto);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
    }

    @DisplayName("사용자가 유효하지 않은 경우 예외가 발생한다. - 500 예외")
    @Test
    void showRepositories_InvalidUsername_404Exception() {
        // given
        String invalidUserName = "invalidUser";

        RepositoryRequestDto requestDto =
            new RepositoryRequestDto(ACCESS_TOKEN, invalidUserName);

        // then
        assertThatThrownBy(() ->
            postService.showRepositories(requestDto)
        ).isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
    }

    @DisplayName("저장된 게시물 중 3, 4번째 글을 최신날짜순으로 가져온다.")
    @Test
    void readHomeFeed_Success() {
        //given
        createMockPosts();

        LoginUser loginUser = new LoginUser("kevin", "a");

        HomeFeedRequest homeFeedRequest = HomeFeedRequest.builder()
            .appUser(loginUser)
            .page(1L)
            .limit(2L)
            .build();

        // when
        List<PostResponseDto> postResponseDtos = postService.readHomeFeed(homeFeedRequest);

        //then
        List<String> postNames = postResponseDtos.stream()
            .map(PostResponseDto::getAuthorName)
            .collect(toList());

        List<String> repoNames = extractGithubRepoUrls(postResponseDtos);

        assertThat(postResponseDtos).hasSize(2);
        assertThat(postNames).containsExactly("dani", "ginger");
        assertThat(repoNames).containsExactly("java-racingcar", "jwp-chess");
    }

    private void createMockPosts() {
        List<PostRequestDto> postRequestDtos = PostFactory.mockPostRequestDtos();
        List<User> users = postRequestDtos.stream()
            .map(PostRequestDto::getUsername)
            .map(UserFactory::user)
            .collect(toList());

        IntStream.range(0, users.size())
            .forEach(index -> {
                User user = users.get(index);
                PostRequestDto newPost = postRequestDtos.get(index);

                userRepository.save(user);
                Long postId = postService.write(newPost).getId();

                CommentRequest commentRequest =
                    new CommentRequest(user.getName(), "test comment" + index, postId);
                postService.addComment(commentRequest);
            });
    }

    @DisplayName("내 피드 게시물들만 조회한다.")
    @Test
    void readMyFeed_Success() {
        //given
        User savedUser = userRepository.save(UserFactory.user("kevin"));
        List<PostRequestDto> postRequestDtos = PostFactory.mockPostRequestForAssertingMyFeed();
        postRequestDtos.forEach(postService::write);

        LoginUser loginUser = new LoginUser(savedUser.getName(), "a");

        //when
        HomeFeedRequest homeFeedRequest = HomeFeedRequest.builder()
            .appUser(loginUser)
            .page(0L)
            .limit((long) postRequestDtos.size())
            .build();

        List<PostResponseDto> postResponseDtos = postService.readMyFeed(homeFeedRequest);
        List<String> repoNames = postResponseDtos.stream()
            .map(PostResponseDto::getGithubRepoUrl)
            .collect(toList());

        //then
        assertThat(postResponseDtos).hasSize(postRequestDtos.size());
        assertThat(repoNames).containsAll(extractGithubRepoUrls(postRequestDtos));
    }

    @DisplayName("로그인 사용자가 다른 사용자의 피드 게시물을 조회한다.")
    @Test
    void readUserFeed_LoginUser_Success() {
        //given
        User neozal = userRepository.save(UserFactory.user("neozal"));
        User kevin = userRepository.save(UserFactory.user("kevin"));

        List<PostRequestDto> postRequestDtos =
            PostFactory.mockPostRequestForAssertingMyFeed();
        postRequestDtos.forEach(postService::write);

        LoginUser loginUser = new LoginUser(neozal.getName(), "a");
        HomeFeedRequest homeFeedRequest = HomeFeedRequest.builder()
            .appUser(loginUser)
            .page(0L)
            .limit((long) postRequestDtos.size())
            .build();

        //when
        List<PostResponseDto> postResponseDtos =
            postService.readUserFeed(homeFeedRequest, kevin.getName());

        List<String> repoNames = extractGithubRepoUrls(postResponseDtos);
        List<Boolean> likes = extractLikes(postResponseDtos);

        //then
        assertThat(postResponseDtos).hasSize(postRequestDtos.size());
        assertThat(repoNames).containsAll(extractGithubRepoUrls(postRequestDtos));
        assertThat(likes).containsAll(extractLikes(postResponseDtos));
    }

    @DisplayName("비로그인 사용자가 다른 사용자의 피드 게시물을 조회한다.")
    @Test
    void readUserFeed_GuestUser_Success() {
        User savedUser = userRepository.save(UserFactory.user("kevin"));

        //given
        List<PostRequestDto> postRequestDtos = PostFactory.mockPostRequestForAssertingMyFeed();
        postRequestDtos.forEach(postService::write);

        HomeFeedRequest homeFeedRequest = HomeFeedRequest.builder()
            .appUser(new GuestUser())
            .page(0L)
            .limit((long) postRequestDtos.size())
            .build();

        //when
        List<PostResponseDto> postResponseDtos =
            postService.readUserFeed(homeFeedRequest, savedUser.getName());
        List<String> repoNames = extractGithubRepoUrls(postResponseDtos);
        List<Boolean> likes = extractLikes(postResponseDtos);

        //then
        assertThat(postResponseDtos).hasSize(postRequestDtos.size());
        assertThat(repoNames).containsAll(extractGithubRepoUrls(postRequestDtos));
        assertThat(likes).allMatch(Objects::isNull);
    }

    private List<Boolean> extractLikes(List<PostResponseDto> postResponseDtos) {
        return postResponseDtos.stream()
            .map(PostResponseDto::getLiked)
            .collect(toList());
    }

    private List<String> extractGithubRepoUrls(List<?> dtos) {
        Objects.requireNonNull(dtos);

        return dtos.stream()
            .map(dto -> {
                if (dto instanceof PostResponseDto) {
                    return ((PostResponseDto) dto).getGithubRepoUrl();
                }

                if (dto instanceof PostRequestDto) {
                    return ((PostRequestDto) dto).getGithubRepoUrl();
                }

                throw new IllegalArgumentException();
            }).collect(toList());
    }

    @DisplayName("사용자는 특정 게시물을 좋아요 할 수 있다. - 성공")
    @Test
    void like_ValidUser_Success() {
        // given
        PostRequestDto postRequestDtos = PostFactory.mockPostRequestDtos().get(0);
        User loginUser = userRepository.save(UserFactory.user(postRequestDtos.getUsername()));

        AppUser appUser = new LoginUser(loginUser.getName(), "token");

        PostImageUrlResponseDto writtenPost = postService.write(postRequestDtos);

        // when
        LikeResponseDto likeResponseDto = postService.like(appUser, writtenPost.getId());

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

        PostImageUrlResponseDto writtenPost = postService.write(postRequestDtos);
        postService.like(appUser, writtenPost.getId());

        // when
        LikeResponseDto likeResponseDto = postService.unlike(appUser, writtenPost.getId());

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

        PostImageUrlResponseDto writtenPost = postService.write(postRequestDtos);
        postService.like(appUser, writtenPost.getId());

        // when then
        assertThatThrownBy(() -> postService.like(appUser, writtenPost.getId()))
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

        PostImageUrlResponseDto writtenPost = postService.write(postRequestDtos);

        // when then
        assertThatThrownBy(() -> postService.like(appUser, writtenPost.getId()))
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

        PostImageUrlResponseDto writtenPost = postService.write(postRequestDtos);

        // when then
        assertThatThrownBy(() -> postService.unlike(appUser, writtenPost.getId()))
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

        PostImageUrlResponseDto writtenPost = postService.write(postRequestDtos);
        postService.like(loginUser, writtenPost.getId());

        // when then
        assertThatThrownBy(() -> postService.unlike(guestUser, writtenPost.getId()))
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
        assertThatThrownBy(() -> {
            postService.update(updateRequestDto);
        }).isInstanceOf(PostNotBelongToUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0005")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("해당하는 사용자의 게시물이 아닙니다.");
    }

    @DisplayName("사용자는 게시물을 삭제한다.")
    @Test
    void delete_LoginUser_Success() {
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

        PostDeleteRequestDto deleteRequestDto = new PostDeleteRequestDto(loginUser, 1L);

        // when
        postService.delete(deleteRequestDto);

        // then
        assertThat(postRepository.findById(1L)).isEmpty();
    }

    @DisplayName("해당하는 사용자의 게시물이 아닌 경우 삭제할 수 없다. - 401 예외")
    @Test
    void delete_PostNotBelongToUser_401Exception() {
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

        PostDeleteRequestDto deleteRequestDto = new PostDeleteRequestDto(loginUser, 1L);

        // when
        assertThatThrownBy(() -> {
            postService.delete(deleteRequestDto);
        }).isInstanceOf(PostNotBelongToUserException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0005")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("해당하는 사용자의 게시물이 아닙니다.");
    }
}
