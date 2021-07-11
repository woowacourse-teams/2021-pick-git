package com.woowacourse.pickgit.post.domain;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class PostContent {

    @Lob
    private String content;

    protected PostContent() {
    }
}
