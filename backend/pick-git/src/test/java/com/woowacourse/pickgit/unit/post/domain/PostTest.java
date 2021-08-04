package com.woowacourse.pickgit.unit.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.post.CannotAddTagException;
import com.woowacourse.pickgit.exception.post.CannotUnlikeException;
import com.woowacourse.pickgit.exception.post.DuplicatedLikeException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PostTest {

    @DisplayName("Tag를 정상적으로 Post에 등록한다.")
    @Test
    void addTags_ValidTags_RegistrationSuccess() {
        Post post = Post.builder()
            .content("abc")
            .build();

        List<Tag> tags = List.of(
            new Tag("tag1"),
            new Tag("tag2"),
            new Tag("tag3")
        );

        post.addTags(tags);

        assertThat(post.getTagNames()).hasSize(3);
    }

    @DisplayName("중복되는 이름의 Tag가 존재하면 Post에 추가할 수 없다.")
    @Test
    void addTags_DuplicatedTagName_ExceptionThrown() {
        Post post = Post.builder()
            .content("abc")
            .build();

        List<Tag> tags = List.of(
            new Tag("tag1"),
            new Tag("tag2"),
            new Tag("tag3")
        );

        post.addTags(tags);

        List<Tag> duplicatedTags = List.of(
            new Tag("tag4"),
            new Tag("tag3")
        );

        assertThatCode(() -> post.addTags(duplicatedTags))
            .isInstanceOf(CannotAddTagException.class)
            .extracting("errorCode")
            .isEqualTo("P0001");
    }

    @DisplayName("사용자는 특정 게시물을 좋아요 할 수 있다.")
    @Test
    void like_validUser_Success() {
        Post post = Post.builder()
            .content("abc")
            .build();

        User user = UserFactory.user();

        post.like(user);

        assertThat(post.getLikeCounts()).isEqualTo(1);
        assertThat(post.isLikedBy(user)).isTrue();
    }

    @DisplayName("사용자는 특정 게시물을 좋아요 취소 할 수 있다.")
    @Test
    void unlike_validUser_Success() {
        Post post = Post.builder()
            .content("abc")
            .build();

        User user1 = UserFactory.user(1L, "user");
        User user2 = UserFactory.user(2L, "another User");

        post.like(user1);
        post.like(user2);

        assertThat(post.getLikeCounts()).isEqualTo(2);
        assertThat(post.isLikedBy(user1)).isTrue();
        assertThat(post.isLikedBy(user2)).isTrue();

        post.unlike(user2);

        assertThat(post.getLikeCounts()).isEqualTo(1);
        assertThat(post.isLikedBy(user1)).isTrue();
        assertThat(post.isLikedBy(user2)).isFalse();
    }

    @DisplayName("사용자는 이미 좋아요한 게시물을 좋아요 추가 할 수 없다.")
    @Test
    void like_AlreadyLikePost_ExceptionThrown() {
        Post post = Post.builder()
            .content("abc")
            .build();

        User user = UserFactory.user(1L, "user");
        post.like(user);

        assertThatThrownBy(() -> post.like(user))
            .isInstanceOf(DuplicatedLikeException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0003")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("이미 좋아요한 게시물 중복 좋아요 에러");
    }

    @DisplayName("사용자는 좋아요 하지 않은 게시물을 좋아요 취소 할 수 없다.")
    @Test
    void unlike_nonLikePost_ExceptionThrown() {
        Post post = Post.builder()
            .content("abc")
            .build();

        User user = UserFactory.user(1L, "user");

        assertThatThrownBy(() -> post.unlike(user))
            .isInstanceOf(CannotUnlikeException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0004")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("좋아요 하지 않은 게시물 좋아요 취소 에러");
    }
}
