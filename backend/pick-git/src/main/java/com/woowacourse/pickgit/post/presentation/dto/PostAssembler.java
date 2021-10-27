package com.woowacourse.pickgit.post.presentation.dto;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.post.application.dto.request.PostDeleteRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostUpdateRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchRepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeUsersResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostUpdateResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.request.PostRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.PostUpdateRequest;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeUsersResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.PostResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.PostUpdateResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.RepositoryResponse;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Pageable;

public class PostAssembler {

    public static PostRequestDto postRequestDto(AppUser user, PostRequest request) {
        return PostRequestDto.builder()
            .token(user.getAccessToken())
            .username(user.getUsername())
            .images(request.getImages())
            .githubRepoUrl(request.getGithubRepoUrl())
            .tags(request.getTags())
            .content(request.getContent())
            .build();
    }

    public static RepositoryRequestDto repositoryRequestDto(AppUser user, Pageable pageable) {
        return RepositoryRequestDto.builder()
            .token(user.getAccessToken())
            .username(user.getUsername())
            .pageable(pageable)
            .build();
    }

    public static List<RepositoryResponse> repositoryResponses(
        List<RepositoryResponseDto> repositoryResponseDos
    ) {
        return repositoryResponseDos.stream()
            .map(repositoryResponse())
            .collect(toList());
    }

    private static Function<RepositoryResponseDto, RepositoryResponse> repositoryResponse() {
        return repositoryResponseDto -> RepositoryResponse.builder()
            .url(repositoryResponseDto.getUrl())
            .name(repositoryResponseDto.getName())
            .build();
    }

    public static SearchRepositoryRequestDto searchRepositoryRequestDto(
        AppUser user,
        String keyword,
        Pageable pageable
    ) {
        return SearchRepositoryRequestDto.builder()
            .token(user.getAccessToken())
            .username(user.getUsername())
            .keyword(keyword)
            .pageable(pageable)
            .build();
    }

    public static LikeResponse likeResponse(LikeResponseDto likeResponseDto) {
        return LikeResponse.builder()
            .likesCount(likeResponseDto.getLikesCount())
            .liked(likeResponseDto.getLiked())
            .build();
    }

    public static PostUpdateRequestDto postUpdateRequestDto(
        AppUser user,
        Long postId,
        PostUpdateRequest updateRequest
    ) {
        return PostUpdateRequestDto.builder()
            .username(user.getUsername())
            .postId(postId)
            .tags(updateRequest.getTags())
            .content(updateRequest.getContent())
            .build();
    }

    public static PostUpdateResponse postUpdateResponse(PostUpdateResponseDto updateResponseDto) {
        return PostUpdateResponse.builder()
            .tags(updateResponseDto.getTags())
            .content(updateResponseDto.getContent())
            .build();
    }

    public static PostDeleteRequestDto postDeleteRequestDto(AppUser user, Long postId) {
        return new PostDeleteRequestDto(user, postId);
    }

    public static List<LikeUsersResponse> likeUsersResponses(
        List<LikeUsersResponseDto> likeUsersResponseDtos
    ) {
        return likeUsersResponseDtos.stream()
            .map(PostAssembler::likeUserResponse)
            .collect(toList());
    }

    private static LikeUsersResponse likeUserResponse(LikeUsersResponseDto dto) {
        return LikeUsersResponse.builder()
            .username(dto.getUsername())
            .imageUrl(dto.getImageUrl())
            .following(dto.getFollowing())
            .build();
    }

    public static PostResponse postResponse(PostResponseDto postResponseDto) {
        return postResponseDtoPostResponse().apply(postResponseDto);
    }

    public static List<PostResponse> postResponses(List<PostResponseDto> postResponseDtos) {
        return postResponseDtos.stream()
            .map(postResponseDtoPostResponse())
            .collect(toList());
    }

    private static Function<PostResponseDto, PostResponse> postResponseDtoPostResponse() {
        return postResponseDto -> PostResponse.builder()
            .id(postResponseDto.getId())
            .imageUrls(postResponseDto.getImageUrls())
            .githubRepoUrl(postResponseDto.getGithubRepoUrl())
            .content(postResponseDto.getContent())
            .authorName(postResponseDto.getAuthorName())
            .profileImageUrl(postResponseDto.getProfileImageUrl())
            .likesCount(postResponseDto.getLikesCount())
            .tags(postResponseDto.getTags())
            .createdAt(postResponseDto.getCreatedAt())
            .updatedAt(postResponseDto.getUpdatedAt())
            .comments(postResponseDto.getComments())
            .liked(postResponseDto.getLiked())
            .build();
    }
}
