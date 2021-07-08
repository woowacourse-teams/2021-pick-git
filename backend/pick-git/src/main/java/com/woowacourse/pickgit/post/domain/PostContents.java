package com.woowacourse.pickgit.post.domain;

import javax.persistence.Embeddable;

@Embeddable
public class PostContents {

    private String content;

    protected PostContents() {
    }
}
