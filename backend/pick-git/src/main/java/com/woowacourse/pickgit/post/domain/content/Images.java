package com.woowacourse.pickgit.post.domain.content;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Images {

    @OneToMany(mappedBy = "post")
    private List<Image> images = new ArrayList<>();

    protected Images() {
    }

    public Images(List<Image> images) {
        this.images = images;
    }
}
