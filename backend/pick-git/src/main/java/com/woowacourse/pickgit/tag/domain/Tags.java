package com.woowacourse.pickgit.tag.domain;

import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Tags {

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Tag> tags;
}
