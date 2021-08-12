package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.comment.domain.Comments;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.content.Image;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.post.domain.content.PostContent;
import com.woowacourse.pickgit.post.domain.like.Likes;
import com.woowacourse.pickgit.post.domain.tag.PostTags;
import com.woowacourse.pickgit.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;

public class MockPost {

    private MockPost() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private User user;
        private Images images =
            new Images(
                List.of(new Image("www.image1.com"), new Image("www.image2.com"))
            );
        private PostContent content =
            new PostContent("Post content");
        private Likes likes =
            new Likes();
        private Comments comments =
            new Comments();
        private PostTags postTags =
            new PostTags();
        private String githubRepoUrl =
            "www.github.com/pick-git";
        private LocalDateTime createdAt =
            LocalDateTime.of(2000, 2, 2, 2, 2);
        private LocalDateTime updatedAt =
            LocalDateTime.of(2000, 2, 2, 2, 2);

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder images(Images images) {
            this.images = images;
            return this;
        }

        public Builder postContents(PostContent postContent) {
            this.content = postContent;
            return this;
        }

        public Builder likes(Likes likes) {
            this.likes = likes;
            return this;
        }

        public Builder comments(Comments comments) {
            this.comments = comments;
            return this;
        }

        public Builder postTags(PostTags postTags) {
            this.postTags = postTags;
            return this;
        }

        public Builder githubRepoUrl(String githubRepoUrl) {
            this.githubRepoUrl = githubRepoUrl;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Post build() {
            return new Post(
                id,
                user,
                images,
                content,
                likes,
                comments,
                postTags,
                githubRepoUrl,
                createdAt,
                updatedAt
            );
        }
    }

}
