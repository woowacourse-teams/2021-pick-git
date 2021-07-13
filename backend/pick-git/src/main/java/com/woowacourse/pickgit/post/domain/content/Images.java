package com.woowacourse.pickgit.post.domain.content;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Images {

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();

    protected Images() {
    }

    public Images(List<Image> images) {
        this.images = images;
    }
}
