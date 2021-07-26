package com.woowacourse.pickgit.post.domain;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.exception.post.CannotAddTagException;
import com.woowacourse.pickgit.post.domain.comment.Comment;
import com.woowacourse.pickgit.post.domain.comment.Comments;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.post.domain.content.PostContent;
import com.woowacourse.pickgit.post.domain.like.Likes;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.user.domain.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Images images;

    @Embedded
    private PostContent content;

    private String githubRepoUrl;

    @Embedded
    private Likes likes;

    @Embedded
    private Comments comments;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<PostTag> postTags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected Post() {
    }

    public Post(Long id, Images images, PostContent content, String githubRepoUrl,
        Likes likes, Comments comments,
        List<PostTag> postTags, User user) {
        this.id = id;
        this.images = images;
        this.content = content;
        this.githubRepoUrl = githubRepoUrl;
        this.likes = likes;
        this.comments = comments;
        this.postTags = postTags;
        this.user = user;
    }

    public Post(PostContent content, Images images, String githubRepoUrl, User user) {
        this.content = content;
        this.images = images;
        this.githubRepoUrl = githubRepoUrl;
        this.user = user;
        if (!Objects.isNull(images)) {
            images.setMapping(this);
        }
    }

    public void addComment(Comment comment) {
        comment.toPost(this);
        comments.addComment(comment);
    }

    public Long getId() {
        return id;
    }

    public List<String> getImageUrls() {
        return images.getUrls();
    }

    public void addTags(List<Tag> tags) {
        validateDuplicateTag(tags);
        List<Tag> existingTags = getTags();
        for (Tag tag : tags) {
            if (existingTags.contains(tag)) {
                throw new CannotAddTagException();
            }
            PostTag postTag = new PostTag(this, tag);
            postTags.add(postTag);
        }
    }

    private void validateDuplicateTag(List<Tag> tags) {
        Set<String> nonDuplicatetagNames = tags.stream()
            .map(Tag::getName)
            .collect(Collectors.toSet());
        if (nonDuplicatetagNames.size() != tags.size()) {
            throw new CannotAddTagException();
        }
    }

    public List<Tag> getTags() {
        return postTags.stream()
            .map(PostTag::getTag)
            .collect(toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public List<String> getImagaeUrls() {
        return images.getImageUrls();
    }

    public String getGithubRepoUrl() {
        return githubRepoUrl;
    }

    public int getLikeCounts() {
        return likes.getCounts();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getContent() {
        return content.getContent();
    }

    public User getUser() {
        return user;
    }

    public List<Comment> getComments() {
        return comments.getComments();
    }

    public boolean isLikedBy(String userName) {
        return likes.contains(userName);
    }
}
