package com.woowacourse.pickgit.post.domain;

import com.woowacourse.pickgit.post.domain.comment.Comments;
import com.woowacourse.pickgit.post.domain.content.Pictures;
import com.woowacourse.pickgit.post.domain.like.Likes;
import com.woowacourse.pickgit.tag.domain.Tags;
import com.woowacourse.pickgit.user.domain.User;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
public class Post {

    @Embedded
    private Pictures pictures;

    @Embedded
    private PostContents contents;

    @Embedded
    private Likes likes;

    @Embedded
    private Comments comments;

    @Embedded
    private Tags tags;

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    protected Post() {
    }
}
