package com.woowacourse.pickgit.comment.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.request.QueryCommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.presentation.dto.CommentAssembler;
import com.woowacourse.pickgit.comment.presentation.dto.request.ContentRequest;
import com.woowacourse.pickgit.comment.presentation.dto.response.CommentResponse;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForLoginAndGuestUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForOnlyLoginUser;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@CrossOrigin(value = "*")
@RequestMapping("/api")
@RestController
public class CommentController {

    private final CommentService commentService;

    @ForLoginAndGuestUser
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> comment(
        @Authenticated AppUser appUser,
        @PathVariable Long postId,
        @PageableDefault Pageable pageable
    ) {
        QueryCommentRequestDto queryCommentRequestDto =
            CommentAssembler.queryCommentRequestDto(appUser, postId, pageable);

        List<CommentResponseDto> commentResponseDtos =
            commentService.queryComments(queryCommentRequestDto);

        return ResponseEntity.ok(CommentAssembler.commentRequests(commentResponseDtos));
    }

    @ForOnlyLoginUser
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
        @Authenticated AppUser user,
        @PathVariable Long postId,
        @Valid @RequestBody ContentRequest request
    ) {
        CommentRequestDto commentRequestDto = CommentAssembler.commentRequestDto(user, postId, request);
        CommentResponseDto commentResponseDto = commentService.addComment(commentRequestDto);
        CommentResponse commentResponse = CommentAssembler.commentResponse(commentResponseDto);

        return ResponseEntity.ok(commentResponse);
    }

    @ForOnlyLoginUser
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> delete(
        @Authenticated AppUser user,
        @PathVariable Long postId,
        @PathVariable Long commentId
    ) {
        commentService.delete(CommentAssembler.commentDeleteRequestDto(user, postId, commentId));

        return ResponseEntity.noContent().build();
    }
}
