package com.woowacourse.pickgit.comment.application.dto;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.domain.Comment;
import java.util.List;

public class CommentDtoAssembler {

    public static List<CommentResponseDto> commentResponseDtos(List<Comment> comments) {
        return comments.stream()
            .map(CommentDtoAssembler::commentResponseDto)
            .collect(toList());
    }

    public static CommentResponseDto commentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
            .id(comment.getId())
            .profileImageUrl(comment.getProfileImageUrl())
            .authorName(comment.getAuthorName())
            .content(comment.getContent())
            .liked(false)
            .build();
    }
}
