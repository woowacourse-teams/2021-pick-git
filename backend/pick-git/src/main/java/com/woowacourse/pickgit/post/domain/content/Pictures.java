package com.woowacourse.pickgit.post.domain.content;

import static java.util.stream.Collectors.toList;

import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Pictures {

    @OneToMany(mappedBy = "post")
    private List<Picture> pictures;

    public List<String> getUrls() {
        return pictures.stream()
            .map(Picture::getUrl)
            .collect(toList());
    }
}
