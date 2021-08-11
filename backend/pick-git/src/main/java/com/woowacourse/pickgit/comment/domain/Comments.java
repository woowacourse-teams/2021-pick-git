package com.woowacourse.pickgit.comment.domain;

import com.woowacourse.pickgit.exception.comment.CommentNotFoundException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Comments {

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> comments;

    public Comments() {
        this(new ArrayList<>());
    }

    public Comments(List<Comment> comments) {
        this.comments = comments;
    }

    public void add(Comment comment) {
        comments.add(comment);
    }

    public void delete(Post post, User user, Comment comment) {
        comments.stream()
            .filter(postComment -> postComment.equals(comment))
            .findAny()
            .ifPresentOrElse(
                postComment -> postComment.delete(this.getComments(), post, user),
                CommentNotFoundException::new
            );
    }

    public List<Comment> getComments() {
        return comments;
    }
}
