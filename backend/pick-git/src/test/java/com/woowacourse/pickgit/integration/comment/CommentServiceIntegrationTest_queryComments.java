package com.woowacourse.pickgit.integration.comment;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.request.QueryCommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.integration.IntegrationTest;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.DigestUtils;

public class CommentServiceIntegrationTest_queryComments extends IntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private static Stream<Arguments> getParametersForQueryComments() {
        return Stream.of(
            createArguments(1, 3, 3),
            createArguments(0, 1, 1),
            createArguments(2, 4, 6),
            createArguments(0, 6, 0)
        );
    }

    private static Arguments createArguments(int page, int limit, int commentSize) {
        User user = UserFactory.user();

        List<Comment> comments = createUserAndRandomComments(commentSize, user);

        return Arguments.of(page, limit, comments, user);
    }

    private static List<Comment> createUserAndRandomComments(int size, User user) {
        return IntStream
            .range(0, size)
            .mapToObj(i -> new Comment(createRandomString(), user, null))
            .collect(toList());
    }

    private static String createRandomString() {
        String seed = String.valueOf(LocalDateTime.now().getNano());

        return DigestUtils.md5DigestAsHex(seed.getBytes());
    }

    @DisplayName("PostId를 기준으로 comment 목록을 불러올 수 있다.")
    @ParameterizedTest
    @MethodSource("getParametersForQueryComments")
    @Transactional
    void queryComments_UserCanQueryComments_Success(
        int page,
        int limit,
        List<Comment> comments,
        User commentAuthor
    ) {
        // given
        Long postId = preparingTestFixtures(comments, commentAuthor);

        QueryCommentRequestDto queryCommentRequestDto =
            createQueryCommentRequestDto(postId, PageRequest.of(page, limit));

        // when
        List<CommentResponseDto> commentResponsesDto = commentService
            .queryComments(queryCommentRequestDto);

        // then
        List<CommentResponseDto> expected = createExpected(comments, page, limit);

        assertThat(commentResponsesDto)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expected);
    }

    private Long preparingTestFixtures(List<Comment> comments, User commentAuthor) {
        userRepository.save(commentAuthor);

        Post post = Post.builder()
            .build();
        Post savedPost = postRepository.save(post);

        comments.forEach(
            comment -> commentService.addComment(
                CommentRequestDto.builder()
                    .userName(commentAuthor.getName())
                    .content(comment.getContent())
                    .postId(savedPost.getId())
                    .build()
            )
        );

        return savedPost.getId();
    }

    private QueryCommentRequestDto createQueryCommentRequestDto(Long postId, Pageable pageable) {
        return QueryCommentRequestDto.builder()
            .postId(postId)
            .isGuest(false)
            .pageable(pageable)
            .build();
    }

    private List<CommentResponseDto> createExpected(List<Comment> comments, int page, int limit) {
        List<CommentResponseDto> commentResponsesDto = comments.stream()
            .map(this::createCommentResponseDto)
            .collect(toList());

        return IntStream
            .range(page * limit, Math.min(commentResponsesDto.size(), page * limit + limit))
            .mapToObj(commentResponsesDto::get)
            .collect(toList());
    }

    private CommentResponseDto createCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
            .id(comment.getId())
            .profileImageUrl(comment.getProfileImageUrl())
            .authorName(comment.getAuthorName())
            .content(comment.getContent())
            .liked(false)
            .build();
    }
}
