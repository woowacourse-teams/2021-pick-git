package com.woowacourse.pickgit.unit.comment.application;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.application.dto.request.QueryCommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.comment.domain.CommentRepository;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.DigestUtils;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest_queryComments {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    private static Stream<Arguments> getParametersForQueryComments() {
        return Stream.of(
            Arguments.of(1L, 0, 5, createRandomComments(5)),
            Arguments.of(1L, 0, 3, createRandomComments(3)),
            Arguments.of(1L, 0, 1, createRandomComments(1)),
            Arguments.of(1L, 0, 6, createRandomComments(6)),
            Arguments.of(1L, 0, 6, createRandomComments(0))
        );
    }

    private static List<Comment> createRandomComments(int size) {
        User user = UserFactory.user();

        return IntStream.range(0, size)
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
    void queryComments_UserCanQueryComments_Success(Long postId, int page, int limit,
        List<Comment> comments) {
        // given
        given(commentRepository.findCommentsByPost_Id(anyLong(), any(Pageable.class)))
            .willReturn(comments);

        QueryCommentRequestDto queryCommentRequestDto =
            createQueryCommentRequestDto(postId, PageRequest.of(page, limit));

        // when
        List<CommentResponseDto> commentResponsesDto = commentService
            .queryComments(queryCommentRequestDto);

        // then
        List<CommentResponseDto> expected = createExpected(comments);

        assertThat(commentResponsesDto)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private QueryCommentRequestDto createQueryCommentRequestDto(Long postId, Pageable pageable) {
        return QueryCommentRequestDto.builder()
            .postId(postId)
            .isGuest(false)
            .pageable(pageable)
            .build();
    }

    private List<CommentResponseDto> createExpected(List<Comment> comments) {
        return comments.stream()
            .map(this::createCommentResponseDto)
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
