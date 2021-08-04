package com.woowacourse.pickgit.unit.post.domain.content;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.content.Image;
import com.woowacourse.pickgit.post.domain.content.Images;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ImagesTest {

    private List<Image> imageObjects;
    private Images images;

    @BeforeEach
    void setUp() {
        imageObjects = List.of(new Image("imageUrl1"),
            new Image("imageUrl2"),
            new Image("imageUrl3"),
            new Image("imageUrl4"));

        images = new Images(imageObjects);
    }

    @DisplayName("이미지들의 url을 리스트로 받아온다.")
    @Test
    void getUrls() {
        //when
        List<String> actual = images.getUrls();

        //then
        List<String> expected = imageObjects.stream()
            .map(Image::getUrl)
            .collect(toList());

        assertThat(expected).containsAll(actual);
    }

    @DisplayName("각 이미지들을 연관관계가 만들어질 post와 바인딩 한다.")
    @Test
    void setMapping() throws NoSuchFieldException, IllegalAccessException {
        //when
        Post post = Post.builder().id(1L).build();
        images.belongTo(post);

        List<Post> actual = getMappedPostsOf(images);

        assertThat(actual).allMatch(p -> p.equals(post));
    }

    @SuppressWarnings("unchecked")
    private List<Post> getMappedPostsOf(Images images)
        throws NoSuchFieldException, IllegalAccessException {
        Field imagesField = Images.class.getDeclaredField("images");
        imagesField.setAccessible(true);

        List<Image> imageObjects = (List<Image>) imagesField.get(images);

        return imageObjects.stream()
            .map(this::getPostFrom)
            .collect(toList());
    }

    private Post getPostFrom(Image image) {
        try {
            Field postField = Image.class.getDeclaredField("post");
            postField.setAccessible(true);

            return (Post) postField.get(image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
