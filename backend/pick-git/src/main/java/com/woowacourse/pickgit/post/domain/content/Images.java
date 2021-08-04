package com.woowacourse.pickgit.post.domain.content;

import static java.util.stream.Collectors.toUnmodifiableList;

import com.woowacourse.pickgit.post.domain.Post;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Images {

    @OneToMany(
        mappedBy = "post",
        fetch = FetchType.LAZY,
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
    )
    private List<Image> images = new ArrayList<>();

    protected Images() {
    }

    public Images(List<Image> images) {
        this.images = images;
    }

    public void belongTo(Post post) {
        images.forEach(image -> image.belongTo(post));
    }

    public List<String> getUrls() {
        return images.stream()
            .map(Image::getUrl)
            .collect(toUnmodifiableList());
    }

    public List<String> getImageUrls() {
        return images.stream()
            .map(Image::getUrl)
            .collect(toUnmodifiableList());
    }
}

