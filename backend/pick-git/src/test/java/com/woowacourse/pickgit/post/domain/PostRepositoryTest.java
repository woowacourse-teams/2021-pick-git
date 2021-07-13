package com.woowacourse.pickgit.post.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class PostRepositoryTest {

    private static final String USERNAME = "dani";

    @Autowired
    private TestEntityManager testEntityManager;

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
    private String content;

    private BasicProfile basicProfile;
    private GithubProfile githubProfile;
    private User user;
    private PostContent postContent;

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
    }

    @DisplayName("게시글을 저장한다.")
    @Test
    void save_SavedPost_Success() {
        // given
        Post post = new Post(postContent, user);

        // when
        userRepository.save(user);
        postRepository.save(post);

        testEntityManager.flush();
        testEntityManager.clear();

        Post findPost = postRepository.findByUser(user)
            .orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(findPost.getId()).isNotNull();
    }
}
