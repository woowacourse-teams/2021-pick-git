package com.woowacourse.pickgit.unit.post.domain.content;

import com.woowacourse.pickgit.common.factory.PostBuilder;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.content.Image;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class ImageTest {

    @DisplayName("Image와 연관관계를 같는 post를 바인딩 한다.")
    @Test
    void toPost() throws NoSuchFieldException, IllegalAccessException {
        //given
        Post post = new PostBuilder().id(1L).build();
        Image testImageUrl = new Image("testImageUrl");

        //when
        testImageUrl.toPost(post);

        //then
        Field postField = Image.class.getDeclaredField("post");
        postField.setAccessible(true);
        Post fieldPost = (Post) postField.get(testImageUrl);

        assertThat(post).isEqualTo(fieldPost);
    }
}
