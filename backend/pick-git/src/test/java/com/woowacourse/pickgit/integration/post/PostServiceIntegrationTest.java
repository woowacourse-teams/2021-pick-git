package com.woowacourse.pickgit.integration.post;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.exception.post.CannotAddTagException;
import com.woowacourse.pickgit.exception.post.CommentFormatException;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.CommentResponse;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostImageUrlResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoriesResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.post.domain.comment.Comments;
import com.woowacourse.pickgit.post.presentation.dto.request.CommentRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.HomeFeedRequest;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@Import(InfrastructureTestConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class PostServiceIntegrationTest {

    private static final String USERNAME = "jipark3";
    private static final String ACCESS_TOKEN = "oauth.access.token";

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private String image;
    private String description;
    private String githubUrl;
    private String company;
    private String location;
    private String website;
    private String twitter;
    private String githubRepoUrl;
    private List<String> tags;
    private String content;

    private BasicProfile basicProfile;
    private GithubProfile githubProfile;
    private User user1;
    private User user2;
    private Post post;

    @BeforeEach
    void setUp() {
        image = "image1";
        description = "hello";
        githubUrl = "https://github.com/da-nyee";
        company = "woowacourse";
        location = "seoul";
        website = "https://da-nyee.github.io/";
        twitter = "dani";
        githubRepoUrl = "https://github.com/woowacourse-teams/2021-pick-git/";
        tags = List.of("java", "spring");
        content = "this is content";

        basicProfile = new BasicProfile(USERNAME, image, description);
        githubProfile = new GithubProfile(githubUrl, company, location, website, twitter);
        user1 = new User(basicProfile, githubProfile);
        user2 = new User(new BasicProfile("kevin", "a.jpg", "a"),
            new GithubProfile("github.com", "a", "a", "a", "a"));
        post = new Post(null, null, null, null,
            null, new Comments(), new ArrayList<>(), null);

        userRepository.save(user1);
        userRepository.save(user2);
        postRepository.save(post);
    }

    @DisplayName("Post에 Comment를 정상 등록한다.")
    @Test
    void addComment_ValidContent_Success() {
        // given
        post = new Post(null, null, null, null, null, new Comments(), new ArrayList<>(), null);
        Post savedPost = postRepository.save(post);

        // when
        CommentRequest commentRequest = CommentRequest.builder()
            .userName("kevin")
            .content("test comment")
            .postId(savedPost.getId())
            .build();
        CommentResponse commentResponseDto = postService.addComment(commentRequest);

        // then
        assertThat(commentResponseDto.getAuthorName()).isEqualTo("kevin");
        assertThat(commentResponseDto.getContent()).isEqualTo("test comment");
    }

    @DisplayName("Post에 빈 Comment은 등록할 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void addComment_InvalidContent_ExceptionThrown(String content) {
        // given
        post = new Post(null, null, null, null, null, new Comments(), new ArrayList<>(), null);
        Post savedPost = postRepository.save(post);

        // when
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
        // when
        CommentRequest commentRequest = CommentRequest.builder()
            .userName("kevin")
            .content(content)
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
        post = new Post(null, null, null, null, null, new Comments(), new ArrayList<>(), null);
        Post savedPost = postRepository.save(post);

        // when
        CommentRequest commentRequest = CommentRequest.builder()
            .userName("anonymous")
            .content(content)
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
        PostRequestDto requestDto =
            new PostRequestDto(ACCESS_TOKEN, USERNAME,
                List.of(
                    FileFactory.getTestImage1(),
                    FileFactory.getTestImage2()
                ), githubRepoUrl, tags, content);

        // when
        PostImageUrlResponseDto responseDto = postService.write(requestDto);

        // then
        assertThat(responseDto.getId()).isNotNull();
    }

    @DisplayName("사용자는 게시글 등록시 중복된 태그를 작성할 수 없다.")
    @Test
    void write_LoginUserWithDuplicateTag_Fail() {
        // given
        PostRequestDto requestDto = PostRequestDto.builder()
            .token(ACCESS_TOKEN)
            .username(USERNAME)
            .images(
                List.of(
                    FileFactory.getTestImage1(),
                    FileFactory.getTestImage2()
                )
            )
            .githubRepoUrl(githubRepoUrl)
            .tags(List.of("Java", "Javascript", "Java"))
            .content(content).build();

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
        RepositoryRequestDto requestDto =
            new RepositoryRequestDto(ACCESS_TOKEN + "hi", USERNAME);

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
        RepositoryRequestDto requestDto =
            new RepositoryRequestDto(ACCESS_TOKEN, USERNAME + "hi");

        // then
        assertThatThrownBy(() -> {
            postService.showRepositories(requestDto);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
    }

    @DisplayName("저장된 게시물 중 3, 4번째 글을 최신날짜순으로 가져온다.")
    @Test
    void readHomeFeed_Success() {
        createMockPosts();

        HomeFeedRequest homeFeedRequest =
            new HomeFeedRequest(new LoginUser("kevin", "a"), 1L, 2L);
        List<PostResponseDto> postResponseDtos = postService.readHomeFeed(homeFeedRequest);

        List<String> postNames = postResponseDtos.stream()
            .map(PostResponseDto::getAuthorName)
            .collect(toList());

        List<String> repoNames = postResponseDtos.stream()
            .map(PostResponseDto::getGithubRepoUrl)
            .collect(toList());

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
        List<PostRequestDto> postRequestDtos = PostFactory.mockPostRequestForAssertingMyFeed();

        for (PostRequestDto postRequestDto : postRequestDtos) {
            postService.write(postRequestDto);
        }

        //when
        HomeFeedRequest homeFeedRequest =
            new HomeFeedRequest(new LoginUser("kevin", "a"), 0L, (long) postRequestDtos.size());
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
        List<PostRequestDto> postRequestDtos =
            PostFactory.mockPostRequestForAssertingMyFeed();

        for (PostRequestDto postRequestDto : postRequestDtos) {
            postService.write(postRequestDto);
        }

        //when
        HomeFeedRequest homeFeedRequest =
            new HomeFeedRequest(
                new LoginUser("ala", "a"),
                0L,
                (long) postRequestDtos.size()
            );
        List<PostResponseDto> postResponseDtos =
            postService.readUserFeed(homeFeedRequest, "kevin");
        List<String> repoNames = postResponseDtos.stream()
            .map(PostResponseDto::getGithubRepoUrl)
            .collect(toList());
        List<Boolean> likes = extractLikes(postResponseDtos);

        //then
        assertThat(postResponseDtos).hasSize(postRequestDtos.size());
        assertThat(repoNames).containsAll(extractGithubRepoUrls(postRequestDtos));
        assertThat(likes).containsAll(extractLikes(postResponseDtos));
    }

    @DisplayName("비로그인 사용자가 다른 사용자의 피드 게시물을 조회한다.")
    @Test
    void readUserFeed_GuestUser_Success() {
        //given
        List<PostRequestDto> postRequestDtos = PostFactory.mockPostRequestForAssertingMyFeed();

        for (PostRequestDto postRequestDto : postRequestDtos) {
            postService.write(postRequestDto);
        }

        //when
        HomeFeedRequest homeFeedRequest =
            new HomeFeedRequest(new GuestUser(), 0L, (long) postRequestDtos.size());
        List<PostResponseDto> postResponseDtos = postService.readUserFeed(homeFeedRequest, "kevin");
        List<String> repoNames = extractGithubRepoUrls(postResponseDtos);
        List<Boolean> likes = extractLikes(postResponseDtos);

        //then
        assertThat(postResponseDtos).hasSize(postRequestDtos.size());
        assertThat(repoNames).containsAll(extractGithubRepoUrls(postRequestDtos));
        assertThat(likes).allMatch(Objects::isNull);
    }

    private List<Boolean> extractLikes(List<PostResponseDto> postResponseDtos) {
        return postResponseDtos.stream()
            .map(PostResponseDto::getIsLiked)
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
}
