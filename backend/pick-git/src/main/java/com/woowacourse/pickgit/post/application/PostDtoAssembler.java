package com.woowacourse.pickgit.post.application;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.post.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.comment.Comment;
import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
import java.util.function.Function;

public class PostDtoAssembler {

    private PostDtoAssembler() {
    }

    public static List<PostResponseDto> assembleFrom(
        User requestUser,
        boolean isGuest,
        List<Post> posts
    ) {
        return posts.stream()
            .map(post -> convertFrom(requestUser, isGuest, post))
            .collect(toList());
    }

    private static PostResponseDto convertFrom(User requestUser, boolean isGuest, Post post) {
        List<String> tags = createTagsFrom(post);
        List<CommentResponseDto> comments = createCommentResponsesFrom(post);

        return PostResponseDto.builder()
            .id(post.getId())
            .imageUrls(post.getImageUrls())
            .githubRepoUrl(post.getGithubRepoUrl())
            .content(post.getContent())
            .authorName(post.getAuthorName())
            .profileImageUrl(post.getAuthorProfileImage())
            .likesCount(post.getLikeCounts())
            .tags(tags)
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .comments(comments)
            .liked(isLikedBy(requestUser, post, isGuest))
            .build();
    }

    private static List<CommentResponseDto> createCommentResponsesFrom(Post post) {
        return post.getComments()
            .stream()
            .map(toCommentResponse())
            .collect(toList());
    }

    private static Function<Comment, CommentResponseDto> toCommentResponse() {
        return comment -> CommentResponseDto.builder()
            .id(comment.getId())
            .profileImageUrl(comment.getProfileImageUrl())
            .authorName(comment.getAuthorName())
            .content(comment.getContent())
            .liked(false)
            .build();
    }

    private static List<String> createTagsFrom(Post post) {
        return post.getTagNames();
    }

    private static Boolean isLikedBy(User requestUser, Post post, boolean isGuest) {
        if (isGuest) {
            return null;
        }

        return post.isLikedBy(requestUser);
    }
}
