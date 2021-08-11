package com.woowacourse.pickgit.comment.domain;

import com.woowacourse.pickgit.exception.comment.CannotDeleteCommentException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private CommentContent content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    protected Comment() {
    }

    public Comment(String content, User user) {
        this(null, content, user);
    }

    public Comment(Long id, String content, User user) {
        this.id = id;
        this.content = new CommentContent(content);
        this.user = user;
    }

    public void belongTo(Post post) {
        this.post = post;
    }

    public void delete(List<Comment> comments, Post post, User user) {
        if (!post.isWrittenBy(user) && !this.user.equals(user)) {
            throw new CannotDeleteCommentException();
        }
        comments.remove(this);
    }

    public Long getId() {
        return id;
    }

    public String getProfileImageUrl() {
        return user.getImage();
    }

    public String getAuthorName() {
        return user.getName();
    }

    public String getContent() {
        return content.getContent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
