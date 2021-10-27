package com.woowacourse.pickgit.unit.post.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.JpaTestConfiguration;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.like.Like;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@Import(JpaTestConfiguration.class)
@DataJpaTest
class PostRepositoryTest_likeUsers {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @DisplayName("Post를 조회할 때 연결된 중간테이블 Like + Like에 속한 User까지 모두 즉시로딩으로 가져온다.")
    @Test
    void findPostWithLikeUsers_getPost_Success() {
        // given
        PersistenceUnitUtil persistenceUnitUtil =
            entityManagerFactory.getPersistenceUnitUtil();

        User author = UserFactory.user("author");
        userRepository.save(author);

        List<User> likeUsers = UserFactory.mockLikeUsers();
        Post post = PostFactory.post(author);
        likeUsers.forEach(user -> {
            userRepository.save(user);
            post.like(user);
        });

        Post savedPost = postRepository.save(post);
        testEntityManager.flush();
        testEntityManager.clear();

        // when
        Post findPost =
            postRepository.findPostWithLikeUsers(savedPost.getId())
                .orElseThrow(PostNotFoundException::new);

        List<Like> likes = findPost.getLikes().getLikes();

        // then
        assertThat(findPost).isNotNull();
        assertThat(findPost.getLikeCounts()).isEqualTo(likeUsers.size());
        likes.forEach(like ->
            assertThat(persistenceUnitUtil.isLoaded(like, "user")).isTrue()
        );


    }

    @DisplayName("좋아요가 없는 게시물도 가져온다.")
    @Test
    void findPostWithLikeUsers_noLikes_Success() {
        // given
        User author = UserFactory.user("author");
        userRepository.save(author);

        Post savedPost = postRepository.save(PostFactory.post(author));
        testEntityManager.flush();
        testEntityManager.clear();

        // when
        Post findPost =
            postRepository.findPostWithLikeUsers(savedPost.getId())
                .orElseThrow(PostNotFoundException::new);

        // then
        assertThat(findPost).isNotNull();
    }
}
