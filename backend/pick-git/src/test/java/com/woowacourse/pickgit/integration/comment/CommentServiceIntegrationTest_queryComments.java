package com.woowacourse.pickgit.integration.comment;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.AssertionsForClassTypes.in;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.application.dto.request.QueryCommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

@SpringBootTest
public class CommentServiceIntegrationTest_queryComments {
    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("PostId를 기준으로 comment 목록을 불러올 수 있다.")
    @ParameterizedTest
    @MethodSource("getParametersForQueryComments")
    @Transactional
    void queryComments_UserCanQueryComments_Success(int page, int limit, List<Comment> comments, User commentAuthor) {
        // given
        Long postId = preparingTestFixtures(comments, commentAuthor);

        QueryCommentRequestDto queryCommentRequestDto =
            createQueryCommentRequestDto(postId, limit, page);

        // when
        List<CommentResponseDto> commentResponseDtos = commentService
            .queryComments(queryCommentRequestDto);

        // then
        List<CommentResponseDto> expected = createExpected(comments, page, limit);

        assertThat(commentResponseDtos)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private Long preparingTestFixtures(List<Comment> comments, User commentAuthor) {
        //todo will use this line
        //comments.forEach(commentRepository::save);

        userRepository.save(commentAuthor);

        Post post = Post.builder().build();
        comments.forEach(post::addComment);

        return postRepository.save(post).getId();
    }

    private List<CommentResponseDto> createExpected(List<Comment> comments, int page, int limit) {
        List<CommentResponseDto> commentResponseDtos = comments.stream()
            .map(this::createCommentResponseDto)
            .collect(toList());

        return IntStream.range(page * limit, Math.min(commentResponseDtos.size(), page * limit + limit))
            .mapToObj(commentResponseDtos::get)
            .collect(toList());
    }

    private QueryCommentRequestDto createQueryCommentRequestDto(Long postId, int limit, int page) {
        return QueryCommentRequestDto.builder()
            .postId(postId)
            .isGuest(false)
            .page(page)
            .limit(limit)
            .build();
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

    private static Stream<Arguments> getParametersForQueryComments() {
        return Stream.of(
            createArguments(1,3,3),
            createArguments(0,1,1),
            createArguments(2,4,6),
            createArguments(0,6,0)
        );
    }

    private static Arguments createArguments(int page, int limit, int commentSize) {
        User user = UserFactory.user();
        List<Comment> comments = createUserAndRandomComments(commentSize, user);
        return Arguments.of(page, limit, comments, user);
    }

    private static List<Comment> createUserAndRandomComments(int size, User user) {
        List<Comment> comments = IntStream.range(0, size)
            .mapToObj(i -> new Comment(createRandomString(), user))
            .collect(toList());

        return comments;
    }

    private static String createRandomString() {
        String seed = String.valueOf(LocalDateTime.now().getNano());

        return DigestUtils.md5DigestAsHex(seed.getBytes());
    }
}
