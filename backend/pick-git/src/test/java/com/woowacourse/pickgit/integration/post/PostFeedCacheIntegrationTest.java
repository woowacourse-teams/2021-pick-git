package com.woowacourse.pickgit.integration.post;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.count_data_source.QueryCounter;
import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.List;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.PageRequest;

@Transactional
@EnableCaching
public class PostFeedCacheIntegrationTest extends AcceptanceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostFeedService postFeedService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueryCounter queryCounter;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        toRead();
    }

    @DisplayName("캐싱 - 저예산 환경이므로 캐싱이 동작하지 않는다.")
    @Test
    void readHomeFeed_Guest_LatestPosts() {
        // given
        createMockPosts();
        HomeFeedRequestDto homeFeedRequestDto = createMockGuestHomeFeedRequest();
        entityManager.clear();

        // when
        queryCounter.startCount();
        postFeedService.allHomeFeed(homeFeedRequestDto);

        List<PostResponseDto> postResponseDtos = postFeedService.allHomeFeed(homeFeedRequestDto);

        // then
        assertThat(postResponseDtos)
            .extracting("authorName", "githubRepoUrl", "liked")
            .containsExactly(
                tuple("dani", "java-racingcar", null),
                tuple("ginger", "jwp-chess", null)
            );
        assertThat(queryCounter.getCount().getValue()).isEqualTo(7L);
    }

    private void createMockPosts() {
        List<PostRequestDto> postRequestDtos = PostFactory.mockPostRequestDtos();
        List<User> users = postRequestDtos.stream()
            .map(PostRequestDto::getUsername)
            .map(UserFactory::user)
            .collect(toList());

        IntStream.range(0, users.size())
            .forEach(index -> {
                User user = users.get(index);
                PostRequestDto newPost = postRequestDtos.get(index);

                userRepository.save(user);
                Long postId = postService.write(newPost);

                CommentRequestDto commentRequestDto =
                    new CommentRequestDto(user.getName(), "test comment" + index, postId);
                commentService.addComment(commentRequestDto);
            });
    }

    private HomeFeedRequestDto createMockGuestHomeFeedRequest() {
        return HomeFeedRequestDto.builder()
            .requestUserName(null)
            .isGuest(true)
            .pageable(PageRequest.of(1, 2))
            .build();
    }
}
