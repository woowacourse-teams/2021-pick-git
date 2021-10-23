package com.woowacourse.pickgit.comment.presentation.dto;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.comment.application.dto.request.CommentDeleteRequestDto;
import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.request.QueryCommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.presentation.dto.request.ContentRequest;
import com.woowacourse.pickgit.comment.presentation.dto.response.CommentResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

public class CommentAssembler {

    public static List<CommentResponse> commentRequests(
        List<CommentResponseDto> commentResponseDtos
    ) {
        return commentResponseDtos.stream()
            .map(CommentAssembler::commentResponse)
            .collect(toList());
    }

    public static CommentResponse commentResponse(CommentResponseDto commentResponseDto) {
        return CommentResponse.builder()
            .id(commentResponseDto.getId())
            .profileImageUrl(commentResponseDto.getProfileImageUrl())
            .content(commentResponseDto.getContent())
            .authorName(commentResponseDto.getAuthorName())
            .liked(commentResponseDto.getLiked())
            .build();
    }

    public static CommentRequestDto commentRequestDto(
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

    public static CommentDeleteRequestDto commentDeleteRequestDto(
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

    public static QueryCommentRequestDto queryCommentRequestDto(
        AppUser appUser,
        Long postId,
        Pageable pageable
    ) {
        return QueryCommentRequestDto.builder()
            .postId(postId)
            .isGuest(appUser.isGuest())
            .pageable(pageable)
            .build();
    }
}
