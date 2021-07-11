package com.woowacourse.pickgit.post.domain.comment;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class CommentContent {

    @Lob
    private String content;

    protected CommentContent() {
    }
}
