package com.woowacourse.pickgit.unit.comment.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.comment.CannotDeleteCommentException;
import com.woowacourse.pickgit.exception.post.CommentFormatException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

class CommentTest {

    @DisplayName("100자 이하의 댓글을 생성할 수 있다.")
    @Test
    void newComment_Under100Length_Success() {
        // given
        String content = "a".repeat(99);

        // when, then
        assertThatCode(() -> new Comment(content, null, null))
            .doesNotThrowAnyException();
    }

    @DisplayName("100자 초과의 댓글을 생성할 수 없다.")
    @Test
    void newComment_Over100Length_ExceptionThrown() {
        // given
        String content = "a".repeat(100);

        // when, then
        assertThatCode(() -> new Comment(content, null, null))
            .isInstanceOf(CommentFormatException.class)
            .extracting("errorCode")
            .isEqualTo("F0002");
    }

    @DisplayName("댓글은 null이거나 빈 문자열(공백만 있는 문자열 포함)이면 생성할 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void newComment_NullOrEmpty_ExceptionThrown(String content) {
        // when, then
        assertThatCode(() -> new Comment(content, null, null))
            .isInstanceOf(CommentFormatException.class)
            .extracting("errorCode")
            .isEqualTo("F0002");
    }

    @DisplayName("내 게시물, 내 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByMeAndIsCommentedByMe_Success() {
        // given
        User me = UserFactory.user(1L, "dani");

        Post postByMe = Post.builder()
            .content("hi")
            .author(me)
            .build();

        Comment comment = new Comment(1L, "comment1", me, postByMe);

        // when
        comment.validateDeletion(postByMe, me);
    }

    @DisplayName("내 게시물, 남 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByMeAndIsCommentedByOther_Success() {
        // given
        User me = UserFactory.user(1L, "dani");
        User other = UserFactory.user(2L, "dada");

        Post postByMe = Post.builder()
            .id(1L)
            .content("hi")
            .author(me)
            .build();

        Comment comment = new Comment(1L, "comment1", other, postByMe);

        // when
        comment.validateDeletion(postByMe, me);
    }

    @DisplayName("남 게시물, 내 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByOtherAndIsCommentedByMe_Success() {
        // given
        User me = UserFactory.user(1L, "dani");
        User other = UserFactory.user(2L, "dada");

        Post postByOther = Post.builder()
            .content("hi")
            .author(other)
            .build();

        Comment comment = new Comment(1L, "comment1", me, postByOther);

        // when
        comment.validateDeletion(postByOther, me);
    }

    @DisplayName("남 게시물, 남 댓글은 삭제할 수 없다. - 401 예외")
    @Test
    void delete_isWrittenByOtherAndIsCommentedByOther_401Exception() {
        // given
        User me = UserFactory.user(1L, "dani");
        User other = UserFactory.user(2L, "dada");

        Post postByOther = Post.builder()
            .content("hi")
            .author(other)
            .build();

        Comment comment = new Comment(1L, "comment2", other, postByOther);

        // when
        assertThatThrownBy(() -> {
            comment.validateDeletion(postByOther, me);
        }).isInstanceOf(CannotDeleteCommentException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0007")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("남 게시물, 남 댓글은 삭제할 수 없습니다.");
    }
}
