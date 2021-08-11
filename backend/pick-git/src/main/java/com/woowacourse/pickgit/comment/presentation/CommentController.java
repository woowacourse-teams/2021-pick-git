package com.woowacourse.pickgit.comment.presentation;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.application.dto.request.CommentDeleteRequestDto;
import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.request.QueryCommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.presentation.dto.request.ContentRequest;
import com.woowacourse.pickgit.comment.presentation.dto.response.CommentResponse;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> comment(
        @Authenticated AppUser appUser,
        @PathVariable Long postId,
        @RequestParam int page,
        @RequestParam int limit
    ) {
        QueryCommentRequestDto queryCommentRequestDto =
            createQueryCommentRequestDto(appUser, postId, page, limit);

        List<CommentResponseDto> commentResponseDtos = commentService
            .queryComments(queryCommentRequestDto);

        return ResponseEntity.ok(createCommentRequestDtos(commentResponseDtos));
    }

    private List<CommentResponse> createCommentRequestDtos(
        List<CommentResponseDto> commentResponseDtos) {
        return commentResponseDtos.stream()
            .map(this::createCommentResponse)
            .collect(toList());
    }

    private QueryCommentRequestDto createQueryCommentRequestDto(AppUser appUser, Long postId,
        int page, int limit) {
        return QueryCommentRequestDto.builder()
            .postId(postId)
            .isGuest(appUser.isGuest())
            .page(page)
            .limit(limit)
            .build();
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
        @Authenticated AppUser user,
        @PathVariable Long postId,
        @Valid @RequestBody ContentRequest request
    ) {
        CommentRequestDto commentRequestDto = createCommentRequest(user, postId, request);
        CommentResponseDto commentResponseDto = commentService.addComment(commentRequestDto);
        CommentResponse commentResponse = createCommentResponse(commentResponseDto);

        return ResponseEntity.ok(commentResponse);
    }

    private CommentRequestDto createCommentRequest(
        AppUser user,
        Long postId,
        ContentRequest request
    ) {
        return CommentRequestDto.builder()
            .userName(user.getUsername())
            .content(request.getContent())
            .postId(postId)
            .build();
    }

    private CommentResponse createCommentResponse(CommentResponseDto commentResponseDto) {
        return CommentResponse.builder()
            .id(commentResponseDto.getId())
            .profileImageUrl(commentResponseDto.getProfileImageUrl())
            .content(commentResponseDto.getContent())
            .authorName(commentResponseDto.getAuthorName())
            .liked(commentResponseDto.getLiked())
            .build();
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> delete(
        @Authenticated AppUser user,
        @PathVariable Long postId,
        @PathVariable Long commentId
    ) {
        commentService.delete(createCommentDeleteRequestDto(user, postId, commentId));

        return ResponseEntity.noContent().build();
    }

    private CommentDeleteRequestDto createCommentDeleteRequestDto(
        AppUser user,
        Long postId,
        Long commentId
    ) {
        return CommentDeleteRequestDto.builder()
            .username(user.getUsername())
            .postId(postId)
            .commentId(commentId)
            .build();
    }
}
