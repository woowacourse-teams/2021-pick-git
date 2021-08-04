package com.woowacourse.pickgit.unit.post.domain.content;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.content.Image;
import java.lang.reflect.Field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ImageTest {

    @DisplayName("Image와 연관관계를 같는 post를 바인딩 한다.")
    @Test
    void toPost() throws NoSuchFieldException, IllegalAccessException {
        //given
        Post post = Post.builder().id(1L).build();
        Image testImageUrl = new Image("testImageUrl");

        //when
        testImageUrl.belongTo(post);

        //then
        Field postField = Image.class.getDeclaredField("post");
        postField.setAccessible(true);
        Post fieldPost = (Post) postField.get(testImageUrl);

        assertThat(post).isEqualTo(fieldPost);
    }
}
