package com.woowacourse.pickgit.post.domain;

import com.woowacourse.pickgit.post.domain.comment.Comments;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.post.domain.like.Likes;
import com.woowacourse.pickgit.user.domain.User;
import java.util.ArrayList;
import java.util.List;
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

    public Post(Images images, PostContent content, User user) {
        this.images = images;
        this.content = content;
        this.user = user;
    }

    public Long getId() {
        return id;
    }
}
