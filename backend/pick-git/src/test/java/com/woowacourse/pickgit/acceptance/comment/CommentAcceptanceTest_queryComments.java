package com.woowacourse.pickgit.acceptance.comment;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.comment.domain.CommentRepository;
import com.woowacourse.pickgit.comment.presentation.dto.response.CommentResponse;
import com.woowacourse.pickgit.common.request_builder.PickGitRequest;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.UserRepository;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

public class CommentAcceptanceTest_queryComments extends AcceptanceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        commentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("사용자는 PostId로 댓글을 가져올 수 있다.")
    @ParameterizedTest
    @MethodSource("getParametersForQueryComments")
    void queryComments_UserCanRequestCommentsOfSpecificPost_Success(int commentSize, int page, int limit) {
        Long postId = createPost();
        List<CommentResponse> allCommentResponses = IntStream.range(0, commentSize)
            .mapToObj(i -> addRandomComment(postId))
            .collect(toList());

        List<CommentResponse> actual = PickGitRequest
            .get("/api/posts/{postId}/comments?page={page}&limit={limit}", postId, page, limit)
            .withUser()
            .extract()
            .as(new TypeRef<>() {
            });

        List<CommentResponse> expected = createExpected(allCommentResponses, page, limit);

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expected);
    }

    @DisplayName("게스트는 PostId로 댓글을 가져올 수 있다.")
    @ParameterizedTest
    @MethodSource("getParametersForQueryComments")
    void queryComments_GuestCanRequestCommentsOfSpecificPost_Success(int commentSize, int page, int limit) {
        Long postId = createPost();
        List<CommentResponse> allCommentResponses = IntStream.range(0, commentSize)
            .mapToObj(i -> addRandomComment(postId))
            .collect(toList());

        List<CommentResponse> actual = PickGitRequest
            .get("/api/posts/{postId}/comments?page={page}&limit={limit}", postId, page, limit)
            .withGuest()
            .extract()
            .as(new TypeRef<>() {
            });

        List<CommentResponse> expected = createExpected(allCommentResponses, page, limit);

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expected);
    }

    private List<CommentResponse> createExpected(List<CommentResponse> commentResponses, int page, int limit) {
        return IntStream.range(page * limit, Math.min(commentResponses.size(), page * limit + limit))
            .mapToObj(commentResponses::get)
            .collect(toList());
    }

    private static Stream<Arguments> getParametersForQueryComments() {
        return Stream.of(
            Arguments.of(10, 5, 1),
            Arguments.of(0, 0, 1),
            Arguments.of(10, 0, 1),
            Arguments.of(5, 5, 1)
        );
    }

    private Long createPost() {
        ExtractableResponse<Response> extract = PickGitRequest
            .post("/api/posts")
            .api_posts()
            .withUser()
            .initAllParams()
            .extract();

        String location = extract.header(HttpHeaders.LOCATION);
        String[] split = location.split("/");

        return Long.valueOf(split[split.length-1]);
    }

    private CommentResponse addRandomComment(Long postId) {
        return PickGitRequest
            .post("/api/posts/{postId}/comments", postId)
            .api_posts_postId_comments()
            .withUser()
            .initAllParams()
            .content(createRandomString())
            .extract()
            .as(CommentResponse.class);
    }

    private static String createRandomString() {
        String seed = String.valueOf(LocalDateTime.now().getNano());

        return DigestUtils.md5DigestAsHex(seed.getBytes());
    }
}
