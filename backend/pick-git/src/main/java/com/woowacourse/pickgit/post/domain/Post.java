package com.woowacourse.pickgit.post.domain;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.woowacourse.pickgit.exception.post.CannotAddTagException;
import com.woowacourse.pickgit.post.domain.comment.Comment;
import com.woowacourse.pickgit.post.domain.comment.Comments;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.post.domain.content.PostContent;
import com.woowacourse.pickgit.post.domain.like.Like;
import com.woowacourse.pickgit.post.domain.like.Likes;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.user.domain.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(
        mappedBy = "post",
        fetch = FetchType.LAZY,
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
    )
    private List<PostTag> postTags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected Post() {
    }

    public Post(Images images, PostContent content, String githubRepoUrl, User user) {
        this(
            null, images, content, githubRepoUrl,
            new Likes(), new Comments(), new ArrayList<>(), user
        );
    }

    public Post(
        Long id, Images images, PostContent content, String githubRepoUrl,
        Likes likes, Comments comments, List<PostTag> postTags, User user
    ) {
        this.id = id;
        this.images = images;
        this.content = content;
        this.githubRepoUrl = githubRepoUrl;
        this.likes = likes;
        this.comments = comments;
        this.postTags = postTags;
        this.user = user;

        if (!Objects.isNull(images)) {
            images.setMapping(this);
        }
    }

    public void addComment(Comment comment) {
        comment.toPost(this);
        comments.addComment(comment);
    }

    public void addTags(List<Tag> tags) {
        validateDuplicateTag(tags);
        List<Tag> existingTags = getTags();
        for (Tag tag : tags) {
            validateDuplicateTagAlreadyExistsInPost(existingTags, tag);
            PostTag postTag = new PostTag(this, tag);
            postTags.add(postTag);
        }
    }

    private void validateDuplicateTag(List<Tag> tags) {
        Set<String> nonDuplicateTagNames = tags.stream()
            .map(Tag::getName)
            .collect(toSet());
        if (nonDuplicateTagNames.size() != tags.size()) {
            throw new CannotAddTagException();
        }
    }

    public List<Tag> getTags() {
        return postTags.stream()
            .map(PostTag::getTag)
            .collect(toList());
    }

    public List<String> getTagNames() {
        return postTags.stream()
            .map(PostTag::getTagName)
            .collect(toList());
    }

    private void validateDuplicateTagAlreadyExistsInPost(List<Tag> existingTags, Tag tag) {
        if (existingTags.contains(tag)) {
            throw new CannotAddTagException();
        }
    }

    public void like(User user) {
        Like like = new Like(this, user);
        likes.add(like);
    }

    public void unlike(User user) {
        Like like = new Like(this, user);
        likes.remove(like);
    }

    public boolean isLikedBy(String userName) {
        return likes.contains(userName);
    }

    public Long getId() {
        return id;
    }

    public Post update(String content, List<Tag> tags) {
        postTags.clear();

        this.content = new PostContent(content);
        addTags(tags);

        return this;
    }

    public List<String> getImageUrls() {
        return images.getUrls();
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
}
