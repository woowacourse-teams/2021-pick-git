package com.woowacourse.pickgit.post.application;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.common.FileFactory;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostImageUrlResponseDto;
import com.woowacourse.pickgit.post.domain.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.application.dto.CommentResponse;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostContent;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.exception.post.CommentFormatException;
import com.woowacourse.pickgit.post.domain.comment.Comments;
import com.woowacourse.pickgit.post.domain.content.Image;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.PickGitStorage;
import com.woowacourse.pickgit.post.presentation.dto.request.CommentRequest;
import com.woowacourse.pickgit.tag.application.TagService;
import com.woowacourse.pickgit.tag.application.TagsDto;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    private static final String USERNAME = "dani";
    private static final String ACCESS_TOKEN = "pickgit";

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

    @Mock
    private EntityManager entityManager;

    private String image;
    private String description;
    private String githubUrl;
    private String company;
    private String location;
    private String website;
    private String twitter;
    private Images images;
    private String githubRepoUrl;
    private List<String> tags;
    private String content;

    private BasicProfile basicProfile;
    private GithubProfile githubProfile;
    private User user;

    private PostContent postContent;
    private Post post;

    private BasicProfile basicProfile2;
    private User user2;

    private Post post2;

    @BeforeEach
    void setUp() {
        image = "image1";
        description = "hello";
        githubUrl = "https://github.com/da-nyee";
        company = "woowacourse";
        location = "seoul";
        website = "https://da-nyee.github.io/";
        twitter = "dani";
        images = new Images(getImages());
        githubRepoUrl = "https://github.com/woowacourse-teams/2021-pick-git/";
        tags = List.of("java", "spring");
        content = "this is content";

        basicProfile = new BasicProfile(USERNAME, image, description);
        githubProfile = new GithubProfile(githubUrl, company, location, website, twitter);
        user = new User(basicProfile, githubProfile);

        postContent = new PostContent(content);
        post = new Post(1L, images, postContent, githubRepoUrl,
            null, null, null, user);

        basicProfile2 = new BasicProfile("kevin", "a.jpg", "a");
        user2 = new User(basicProfile2, null);

        post2 = new Post(null, null, null, null,
            null, new Comments(), new ArrayList<>(), null);
    }

    private List<Image> getImages() {
        return List.of("image1", "imgae2").stream()
            .map(Image::new)
            .collect(toList());
    }

    @DisplayName("사용자는 게시물을 등록할 수 있다.")
    @Test
    void write_LoginUser_Success() {
        // given
        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class)))
            .willReturn(post);
        given(pickGitStorage.store(anyList(), anyString()))
            .willReturn(List.of("imageUrl1", "imageUrl2"));
        given(tagService.findOrCreateTags(any()))
            .willReturn(List.of(new Tag("java"), new Tag("spring")));

        PostRequestDto requestDto = getRequestDto();

        // when
        PostImageUrlResponseDto responseDto = postService.write(requestDto);

        // then
        assertThat(responseDto.getId()).isNotNull();
        verify(userRepository, times(1))
            .findByBasicProfile_Name(requestDto.getUsername());
        verify(postRepository, times(1))
            .save(new Post(postContent, any(), githubRepoUrl, user));
        verify(pickGitStorage, times(1))
            .store(anyList(), anyString());
        verify(tagService, times(1))
            .findOrCreateTags(any(TagsDto.class));
    }

    private PostRequestDto getRequestDto() {
        return new PostRequestDto(
            ACCESS_TOKEN,
            USERNAME,
            List.of(
                FileFactory.getTestImage1(),
                FileFactory.getTestImage2()
            ),
            githubRepoUrl,
            tags,
            content
        );
    }

    @DisplayName("게시물에 댓글을 정상 등록한다.")
    @Test
    void addComment_ValidContent_Success() {
        given(postRepository.findById(1L))
            .willReturn(Optional.of(post2));
        given(userRepository.findByBasicProfile_Name("kevin"))
            .willReturn(Optional.of(user2));
        Mockito.doNothing().when(entityManager).flush();

        CommentRequest commentRequest =
            new CommentRequest("kevin", "test comment", 1L);

        CommentResponse commentResponseDto = postService.addComment(commentRequest);

        assertThat(commentResponseDto.getAuthorName()).isEqualTo("kevin");
        assertThat(commentResponseDto.getContent()).isEqualTo("test comment");
        verify(postRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByBasicProfile_Name("kevin");
    }

    @DisplayName("게시물에 빈 댓글을 등록할 수 없다.")
    @Test
    void addComment_InvalidContent_ExceptionThrown() {
        given(postRepository.findById(1L))
            .willReturn(Optional.of(post));
        given(userRepository.findByBasicProfile_Name("kevin"))
            .willReturn(Optional.of(user));

        CommentRequest commentRequest =
            new CommentRequest("kevin", "", 1L);

        assertThatCode(() -> postService.addComment(commentRequest))
            .isInstanceOf(CommentFormatException.class)
            .extracting("errorCode")
            .isEqualTo("F0002");
        verify(postRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByBasicProfile_Name("kevin");
    }

    @DisplayName("사용자는 Repository 목록을 가져올 수 있다.")
    @Test
    void showRepositories_LoginUser_Success() {
        // given
        RepositoryRequestDto requestDto = new RepositoryRequestDto(ACCESS_TOKEN, USERNAME);
        List<RepositoryResponseDto> repositories = List.of(
            new RepositoryResponseDto("pick", "https://github.com/jipark3/pick"),
            new RepositoryResponseDto("git", "https://github.com/jipark3/git")
        );

        given(platformRepositoryExtractor.extract(requestDto.getToken(), requestDto.getUsername()))
            .willReturn(repositories);

        // when
        List<RepositoryResponseDto> responsesDto =
            platformRepositoryExtractor.extract(requestDto.getToken(), requestDto.getUsername());

        // then
        assertThat(responsesDto).containsAll(repositories);
        verify(platformRepositoryExtractor, times(1))
            .extract(requestDto.getToken(), requestDto.getUsername());
    }
}
