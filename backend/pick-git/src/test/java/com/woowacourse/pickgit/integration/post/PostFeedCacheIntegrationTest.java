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

    private static final String USERNAME = "binghe";
    private static final String ACCESS_TOKEN = "oauth.access.token";

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

    @DisplayName("캐싱 - 비로그인 홈피드 조회시 두 번째 조회부터는 캐시 저장소에서 가져온다. (현재 페이지당 쿼리는 6번)")
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
        assertThat(queryCounter.getCount().getValue()).isEqualTo(6L);
    }

    @DisplayName("캐싱 - 회원이 게시글 작성시 비로그인 홈피드 조회 캐싱이 삭제된다. (현재 페이지당 쿼리는 6번)")
    @Test
    void writePost_LoginUser_DeleteAllCache() {
        // given
        createMockPosts();
        postFeedService.allHomeFeed(createMockGuestHomeFeedRequest());

        // when
        User user = UserFactory.user(USERNAME);
        userRepository.save(user);

        PostRequestDto postRequestDto = PostRequestDto.builder()
            .token(ACCESS_TOKEN)
            .username(USERNAME)
            .images(List.of(
                FileFactory.getTestImage1(),
                FileFactory.getTestImage2()
            ))
            .githubRepoUrl("https://github.com/bperhaps")
            .tags(List.of("java", "c++"))
            .content("testContent")
            .build();

        postService.write(postRequestDto);
        entityManager.clear();

        // then
        queryCounter.startCount();
        postFeedService.allHomeFeed(createMockGuestHomeFeedRequest());
        assertThat(queryCounter.getCount().getValue()).isEqualTo(6L);
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
