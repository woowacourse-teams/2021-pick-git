package com.woowacourse.pickgit.post.application.dto;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeUsersResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostUpdateResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class PostDtoAssembler {

    private PostDtoAssembler() {
    }

    public static List<PostResponseDto> postResponseDtos(
        User requestUser,
        List<Post> posts
    ) {
        return posts.stream()
            .map(post -> convertFrom(requestUser, post))
            .collect(toList());
    }

    public static PostResponseDto assembleFrom(
        User requestUser,
        Post post
    ) {
        return convertFrom(requestUser, post);
    }

    private static PostResponseDto convertFrom(User requestUser, Post post) {
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
            .liked(isLikedBy(requestUser, post))
            .build();
    }

    private static List<CommentResponseDto> createCommentResponsesFrom(Post post) {
        return post.getComments()
            .stream()
            .map(toCommentResponse())
            .limit(3)
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

    private static Boolean isLikedBy(User requestUser, Post post) {
        if (Objects.isNull(requestUser)) {
            return null;
        }

        return post.isLikedBy(requestUser);
    }

    public static List<LikeUsersResponseDto> createLikeUsersResponseDtoOfGuest(
        List<User> likeUsers
    ) {
        return likeUsers.stream()
            .map(user ->
                new LikeUsersResponseDto(
                    user.getImage(),
                    user.getName(),
                    null
                )
            ).collect(toList());
    }

    public static List<LikeUsersResponseDto> createLikeUsersResponseDtoOfLoginUser(
        User loginUser,
        List<User> likeUsers
    ) {
        return likeUsers.stream()
            .map(user ->
                new LikeUsersResponseDto(
                    user.getImage(),
                    user.getName(),
                    loginUser.isFollowing(user)
                )
            ).collect(toList());
    }

    public static Post post(User user, PostRequestDto postRequestDto, List<String> imageUrls) {
        return Post.builder()
            .content(postRequestDto.getContent())
            .images(imageUrls)
            .githubRepoUrl(postRequestDto.getGithubRepoUrl())
            .author(user)
            .build();
    }

    public static List<RepositoryResponseDto> repositoryResponsesDtos(
        List<RepositoryNameAndUrl> repositoryNameAndUrls
    ) {
        return repositoryNameAndUrls.stream()
            .map(toRepositoryResponseDto())
            .collect(toList());
    }

    private static Function<RepositoryNameAndUrl, RepositoryResponseDto> toRepositoryResponseDto() {
        return repositoryNameAndUrl -> RepositoryResponseDto.builder()
            .name(repositoryNameAndUrl.getName())
            .url(repositoryNameAndUrl.getUrl())
            .build();
    }

    public static PostUpdateResponseDto postUpdateRequestDto(Post post) {
        return PostUpdateResponseDto.builder()
            .content(post.getContent())
            .tags(post.getTagNames())
            .build();
    }
}
