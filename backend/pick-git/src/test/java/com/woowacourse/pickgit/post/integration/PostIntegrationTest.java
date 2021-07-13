package com.woowacourse.pickgit.post.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.post.application.PostService;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class PostIntegrationTest {

    private static final String USERNAME = "dani";
    private static final String ACCESS_TOKEN = "pickgit";

    @LocalServerPort
    private int port;

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
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

        userRepository.save(user);
    }

    @DisplayName("사용자는 게시물을 등록할 수 있다.")
    @Test
    void write_LoginUser_Success() {
        // given
        PostRequestDto requestDto = new PostRequestDto(ACCESS_TOKEN, USERNAME, images,
            githubRepoUrl, tags, content);

        // when
        PostResponseDto responseDto = postService.write(requestDto);

        // then
        assertThat(responseDto.getId()).isNotNull();
    }
}
