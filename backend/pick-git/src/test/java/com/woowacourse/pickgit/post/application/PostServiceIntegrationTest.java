package com.woowacourse.pickgit.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.common.FileFactory;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoriesResponseDto;
import com.woowacourse.pickgit.post.domain.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.config.StorageConfiguration;
import com.woowacourse.pickgit.post.application.dto.PostDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.post.domain.comment.CommentFormatException;
import com.woowacourse.pickgit.post.domain.comment.Comments;
import com.woowacourse.pickgit.post.infrastructure.GithubRepositoryExtractor;
import com.woowacourse.pickgit.post.infrastructure.MockRepositoryApiRequester;
import com.woowacourse.pickgit.post.presentation.PickGitStorage;
import com.woowacourse.pickgit.post.presentation.dto.HomeFeedRequest;
import com.woowacourse.pickgit.tag.TestTagConfiguration;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@Import({TestTagConfiguration.class, StorageConfiguration.class})
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

    private User kevin;

    @BeforeEach
    void setUp() {
//        platformRepositoryExtractor =
//            new GithubRepositoryExtractor(objectMapper, new MockRepositoryApiRequester());
//        postService = new PostService(
//            userRepository, postRepository, pickGitStorage, platformRepositoryExtractor);

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
        kevin =
            new User(new BasicProfile("kevin", "a.jpg", "a"),
                new GithubProfile("github.com", "a", "a", "a", "a"));
        userRepository.save(kevin);
    }

    @DisplayName("게시물에 댓글을 정상 등록한다.")
    @Test
    void addComment_ValidContent_Success() {
        post = new Post(null, null, null, null, null, new Comments(), new ArrayList<>(), null);
        postRepository.save(post);

        CommentRequestDto commentRequestDto =
            new CommentRequestDto("kevin", "test comment", post.getId());

        CommentResponseDto commentResponseDto = postService.addComment(commentRequestDto);

        assertThat(commentResponseDto.getAuthorName()).isEqualTo("kevin");
        assertThat(commentResponseDto.getContent()).isEqualTo("test comment");
    }

    @DisplayName("게시물에 빈 댓글은 등록할 수 없다.")
    @Test
    void addComment_InvalidContent_ExceptionThrown() {
        post = new Post(null, null, null, null, null, new Comments(), new ArrayList<>(), null);
        postRepository.save(post);

        CommentRequestDto commentRequestDto =
            new CommentRequestDto("kevin", "", post.getId());

        assertThatCode(() -> postService.addComment(commentRequestDto))
            .isInstanceOf(CommentFormatException.class)
            .hasMessage("F0002");
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
        PostResponseDto responseDto = postService.write(requestDto);

        // then
        assertThat(responseDto.getId()).isNotNull();
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

    @DisplayName("토큰이 유효하지 않은 경우 예외가 발생한다. - 401 예외")
    @Test
    void showRepositories_InvalidAccessToken_401Exception() {
        // given
        RepositoryRequestDto requestDto =
            new RepositoryRequestDto(ACCESS_TOKEN + "hi", USERNAME);

        // then
        assertThatThrownBy(() -> {
            postService.showRepositories(requestDto);
        }).isInstanceOf(HttpClientErrorException.class)
            .hasMessageContaining("401");
    }

    @DisplayName("사용자가 유효하지 않은 경우 예외가 발생한다. - 404 예외")
    @Test
    void showRepositories_InvalidUsername_404Exception() {
        // given
        RepositoryRequestDto requestDto =
            new RepositoryRequestDto(ACCESS_TOKEN, USERNAME + "hi");

        // then
        assertThatThrownBy(() -> {
            postService.showRepositories(requestDto);
        }).isInstanceOf(HttpClientErrorException.class)
            .hasMessageContaining("404");
    }

    @DisplayName("저장된 게시물 중 3, 4번째 글을 가져온다.")
    @Test
    void find() {
        createMockPosts();

        HomeFeedRequest homeFeedRequest =
            new HomeFeedRequest(new LoginUser("kevin", "a"), 1L, 2L);
        List<PostDto> postDtos = postService.readHomeFeed(homeFeedRequest);

        List<String> postNames = postDtos.stream()
            .map(PostDto::getAuthorName)
            .collect(Collectors.toList());

        List<String> repoNames = postDtos.stream()
            .map(PostDto::getGithubRepoUrl)
            .collect(Collectors.toList());

        assertThat(postDtos).hasSize(2);
        assertThat(postNames).containsExactly("dani", "coda");
        assertThat(repoNames).containsExactly("java-racingcar", "junit-test");
    }

    private void createMockPosts() {
        List<PostRequestDto> postRequestDtos = PostFactory.mockPostRequestDtos();
        List<User> users = PostFactory.mockUsers();
        for (int i = 0; i < postRequestDtos.size(); i++) {
            userRepository.save(users.get(i));
            PostResponseDto response = postService.write(postRequestDtos.get(i));
            CommentRequestDto commentRequestDto =
                new CommentRequestDto(users.get(i).getName(), "test comment" + i, response.getId());
            postService.addComment(commentRequestDto);
        }
    }
}
