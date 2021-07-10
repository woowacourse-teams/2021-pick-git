package com.woowacourse.pickgit.post.domain.like;

import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.user.domain.User;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    protected Like() {
    }

    public Like(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}
