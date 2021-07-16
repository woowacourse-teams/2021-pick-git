package com.woowacourse.pickgit.post.application;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.post.application.dto.CommentDto;
import com.woowacourse.pickgit.post.application.dto.PostDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;

public class PostDtoAssembler {

    private PostDtoAssembler() {
    }

    public static List<PostDto> assembleFrom(AppUser appUser, List<Post> posts) {
        return posts.stream()
            .map(post -> convertFrom(post, appUser))
            .collect(Collectors.toList());
    }

    private static PostDto convertFrom(Post post, AppUser appUser) {
        User postWriter = post.getUser();
        List<String> tags = post.getTags()
            .stream()
            .map(Tag::getName)
            .collect(Collectors.toList());
        List<CommentDto> comments = post.getComments()
            .stream()
            .map(CommentDto::from)
            .collect(Collectors.toList());

        if (appUser.isGuest()) {
            return new PostDto(post.getId(), post.getImagaeUrls(), post.getGithubRepoUrl(),
                post.getContent(), postWriter.getName(), postWriter.getBasicProfile().getImage(),
                post.getLikeCounts(),
                tags, post.getCreatedAt(), post.getUpdatedAt(), comments, null);
        }

        return new PostDto(post.getId(), post.getImagaeUrls(), post.getGithubRepoUrl(),
            post.getContent(), postWriter.getName(), postWriter.getBasicProfile().getImage(),
            post.getLikeCounts(),
            tags, post.getCreatedAt(), post.getUpdatedAt(), comments, post.isLikedBy(appUser.getUsername()));
    }
}
