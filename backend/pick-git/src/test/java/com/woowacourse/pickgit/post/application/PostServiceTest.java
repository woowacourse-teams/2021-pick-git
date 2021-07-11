package com.woowacourse.pickgit.post.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostContent;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
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

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    private String image;
    private String description;
    private String githubUrl;
    private String company;
    private String location;
    private String website;
    private String twitter;
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
        content = "this is content";

        basicProfile = new BasicProfile(USERNAME, image, description);
        githubProfile = new GithubProfile(githubUrl, company, location, website, twitter);
        user = new User(basicProfile, githubProfile);
        postContent = new PostContent(content);
        post = new Post(postContent, user);
    }

    @DisplayName("사용자는 글을 작성할 수 있다.")
    @Test
    void writePost_LoginUser_Success() {
        // when
        lenient().when(userRepository.findByBasicProfile_Name(any(String.class)))
            .thenReturn(Optional.ofNullable(user));
        lenient().when(postRepository.save(any(Post.class)))
            .thenReturn(post);
    }
}
