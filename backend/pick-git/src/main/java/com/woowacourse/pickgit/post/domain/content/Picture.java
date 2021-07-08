package com.woowacourse.pickgit.post.domain.content;

import com.woowacourse.pickgit.post.domain.Post;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Picture {

    @ManyToOne
    @JoinColumn(name = "POST_ID")
    protected Post post;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String getUrl() {
        return "test";
    }
}
