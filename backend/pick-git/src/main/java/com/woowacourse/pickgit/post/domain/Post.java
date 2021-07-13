package com.woowacourse.pickgit.post.domain;

import com.woowacourse.pickgit.post.domain.comment.Comments;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.post.domain.like.Likes;
import com.woowacourse.pickgit.user.domain.User;
import java.util.ArrayList;
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
import javax.persistence.OneToMany;

@Entity
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Images images;

    @Embedded
    private PostContent content;

    @Embedded
    private Likes likes;

    @Embedded
    private Comments comments;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<PostTag> postTags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected Post() {
    }

    public Post(Long id, Images images, PostContent content,
        Likes likes, Comments comments,
        List<PostTag> postTags, User user) {
        this.id = id;
        this.images = images;
        this.content = content;
        this.likes = likes;
        this.comments = comments;
        this.postTags = postTags;
        this.user = user;
    }

    public Post(PostContent content, User user) {
        this.content = content;
        this.user = user;
    }

    public Long getId() {
        return id;
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
