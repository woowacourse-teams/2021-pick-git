package com.woowacourse.pickgit.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.post.application.dto.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.PostResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostContent;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    private String image;
    private String description;
    private String githubUrl;
    private String company;
    private String location;
    private String website;
    private String twitter;
    private List<String> images;
    private String githubRepoUrl;
    private List<String> tags;
    private String content;

    private BasicProfile basicProfile;
    private GithubProfile githubProfile;
    private User user;
    private PostContent postContent;
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
        images = List.of("image1", "imgae2");
        githubRepoUrl = "https://github.com/woowacourse-teams/2021-pick-git/";
        tags = List.of("java", "spring");
        content = "this is content";

        basicProfile = new BasicProfile(USERNAME, image, description);
        githubProfile = new GithubProfile(githubUrl, company, location, website, twitter);
        user = new User(basicProfile, githubProfile);
        postContent = new PostContent(content);
        post = new Post(1L, null, postContent, null, null, null, user);
    }

    @DisplayName("사용자는 글을 작성할 수 있다.")
    @Test
    void write_LoginUser_Success() {
        // given
        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class)))
            .willReturn(post);

        PostRequestDto requestDto = new PostRequestDto(ACCESS_TOKEN, USERNAME, images,
            githubRepoUrl, tags, content);

        // when
        PostResponseDto responseDto = postService.write(requestDto);

        // then
        assertThat(responseDto.getId()).isNotNull();
        verify(userRepository, times(1))
            .findByBasicProfile_Name(requestDto.getUsername());
        verify(postRepository, times(1))
            .save(new Post(postContent, user));
    }
}
