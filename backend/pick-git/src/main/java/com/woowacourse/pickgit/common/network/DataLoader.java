package com.woowacourse.pickgit.common.network;

import com.woowacourse.pickgit.authentication.infrastructure.JwtTokenProviderImpl;
import com.woowacourse.pickgit.authentication.infrastructure.dao.CollectionOAuthAccessTokenDao;
import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.comment.domain.CommentRepository;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("staging")
@Component
public class DataLoader implements CommandLineRunner {


    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CollectionOAuthAccessTokenDao collectionOAuthAccessTokenDao;
    private final JwtTokenProviderImpl jwtTokenProviderImpl;

    public DataLoader(
        UserRepository userRepository,
        PostRepository postRepository,
        CommentRepository commentRepository,
        CollectionOAuthAccessTokenDao collectionOAuthAccessTokenDao,
        JwtTokenProviderImpl jwtTokenProviderImpl
    ) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.collectionOAuthAccessTokenDao = collectionOAuthAccessTokenDao;
        this.jwtTokenProviderImpl = jwtTokenProviderImpl;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByBasicProfile_Name("tester").isEmpty()) {
            BasicProfile testerProfile = new BasicProfile("tester", "user image", "hi there");
            GithubProfile testerGithub = new GithubProfile(
                "http://github.com/tester",
                "woowacourse",
                "seoul",
                "woowacourse.github.io",
                "no"
            );
            userRepository.save(new User(testerProfile, testerGithub));
        }

        String testerToken = jwtTokenProviderImpl.createToken("tester");
        collectionOAuthAccessTokenDao.insert(testerToken, "dkansk");
        log.error("tester token {}", testerToken);

        long userCount = userRepository.count();
        if (userCount >= 200_000) {
            return;
        }

        for (int i = 0; i < 200_000; i++) {
            String username = UUID.randomUUID().toString();
            BasicProfile basicProfile = new BasicProfile(username, "user image", "hi there");
            GithubProfile githubProfile = new GithubProfile(
                "http://github.com/tester",
                "woowacourse",
                "seoul",
                "woowacourse.github.io",
                "no"
            );
            User user = userRepository.save(new User(basicProfile, githubProfile));
            Post post = new Post.Builder()
                .author(user)
                .content("test post")
                .images(List.of("testimage.png", "testimage2.png"))
                .githubRepoUrl("github.com/tester/abc")
                .build();
            Post save = postRepository.save(post);
            Comment comment = new Comment("test comment", user, save);
            commentRepository.save(comment);
        }

        log.error("tester token {}", testerToken);
    }
}
