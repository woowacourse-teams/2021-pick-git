package com.woowacourse.pickgit.post.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PostRepositoryTest {

    private static final String USERNAME = "dani";

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

    @DisplayName("게시글을 저장한다.")
    @Test
    void save() {
        // given
        Post savedPost = postRepository.save(post);

        // then
        assertThat(savedPost.getId()).isNotNull();
    }
}
