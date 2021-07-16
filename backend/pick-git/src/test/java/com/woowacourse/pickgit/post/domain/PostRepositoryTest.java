package com.woowacourse.pickgit.post.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.TestJpaConfiguration;
import com.woowacourse.pickgit.post.domain.comment.Comment;
import com.woowacourse.pickgit.post.domain.comment.Comments;
import com.woowacourse.pickgit.post.domain.content.Image;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.tag.domain.TagRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@Import({TestJpaConfiguration.class})
@DataJpaTest
class PostRepositoryTest {

    private static final String USERNAME = "dani";

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private String image;
    private String description;
    private String githubUrl;
    private String company;
    private String location;
    private String website;
    private String twitter;
    private String content;
    private String githubRepoUrl;

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
        githubRepoUrl = "https://github.come/da-nyee/myRepo";

        basicProfile = new BasicProfile(USERNAME, image, description);
        githubProfile = new GithubProfile(githubUrl, company, location, website, twitter);
        user = new User(basicProfile, githubProfile);
        postContent = new PostContent(content);
    }

    @DisplayName("게시글을 저장한다.")
    @Test
    void save_SavedPost_Success() {
        // given
        Images images = new Images(List.of(new Image(image)));
        Post post = new Post(postContent, images, githubRepoUrl, user);

        // when
        Post savedPost = postRepository.save(post);

        // then
        assertThat(savedPost.getId()).isNotNull();
    }

    @DisplayName("게시글을 저장하면 자동으로 생성 날짜가 주입된다.")
    @Test
    void save_SavedPostWithCreatedDate_Success() {
        // given
        Images images = new Images(List.of(new Image(image)));
        Post post = new Post(postContent, images, githubRepoUrl, user);

        // when
        Post savedPost = postRepository.save(post);

        assertThat(savedPost.getCreatedAt()).isNotNull();
        assertThat(savedPost.getCreatedAt()).isBefore(LocalDateTime.now());
    }

    @DisplayName("게시글을 저장할 때 태그도 함께 영속화된다.")
    @Test
    void save_WhenSavingPost_TagSavedTogether() {
        Post post =
            new Post(null, null, new PostContent(), githubRepoUrl, null, null, new ArrayList<>(),
                null);
        List<Tag> tags = Arrays.asList(new Tag("tag1"), new Tag("tag2"));
        post.addTags(tags);
        postRepository.save(post);
        Tag entityTag = tagRepository.save(new Tag("33"));
        post.addTags(Arrays.asList(entityTag));

        testEntityManager.flush();
        testEntityManager.clear();

        Post findPost = postRepository.findAll().get(0);

        assertThat(findPost.getTags()).hasSize(3);
        assertThat(tagRepository.findAll()).hasSize(3);
    }

    @DisplayName("Post에 Comment를 추가하면 Comment가 자동 영속화된다.")
    @Test
    void addComment_WhenSavingPost_CommentSavedTogether() {
        Post post =
            new Post(null, null, new PostContent(), githubRepoUrl, null, new Comments(), new ArrayList<>(),
                null);
        Comment comment = new Comment("test comment")
            .toPost(post);
        post.addComment(comment);

        postRepository.save(post);
        testEntityManager.flush();
        testEntityManager.clear();

        Post findPost = postRepository.findById(post.getId())
            .orElseThrow(IllegalArgumentException::new);
        List<Comment> comments = findPost.getComments();

        assertThat(comments).hasSize(1);
    }
}
