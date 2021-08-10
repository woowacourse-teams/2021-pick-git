package com.woowacourse.pickgit.comment.presentation;

import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.presentation.dto.request.ContentRequest;
import com.woowacourse.pickgit.comment.presentation.dto.response.CommentResponse;
import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
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

}
