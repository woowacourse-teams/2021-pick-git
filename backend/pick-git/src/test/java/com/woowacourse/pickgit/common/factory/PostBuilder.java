package com.woowacourse.pickgit.common.factory;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostTag;
import com.woowacourse.pickgit.post.domain.comment.Comments;
import com.woowacourse.pickgit.post.domain.content.Image;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.post.domain.content.PostContent;
import com.woowacourse.pickgit.post.domain.like.Likes;
import com.woowacourse.pickgit.user.domain.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostBuilder {

    private Long id;
    private Images images = new Images(List.of());
    private PostContent content;
    private String githubRepoUrl;
    private Likes likes = new Likes();
    private Comments comments = new Comments();
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private List<PostTag> postTags = new ArrayList<>();
    private User user;

    public PostBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public PostBuilder images(String... imageUrl) {
        return images(List.of(imageUrl));
    }

    public PostBuilder images(List<String> imageUrls) {
        List<Image> images = imageUrls.stream()
            .map(Image::new)
            .collect(toList());
        this.images = new Images(images);
        return this;
    }

    public PostBuilder content(String content) {
        this.content = new PostContent(content);
        return this;
    }

    public PostBuilder githubRepoUrl(String githubRepoUrl) {
        this.githubRepoUrl = githubRepoUrl;
        return this;
    }

    public PostBuilder likes(Likes likes) {
        this.likes = likes;
        return this;
    }

    public PostBuilder comments(String... comment) {
        Comments comments = new Comments();
        this.comments = comments;
        return this;
    }

    public PostBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public PostBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public PostBuilder postTags(List<PostTag> postTags) {
        this.postTags = postTags;
        return this;
    }

    public PostBuilder user(User user) {
        this.user = user;
        return this;
    }

    public Post build() {
        return new Post(
            id,
            images,
            content,
            githubRepoUrl,
            likes,
            comments,
            postTags,
            user
        );
    }
}
