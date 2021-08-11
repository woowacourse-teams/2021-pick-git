package com.woowacourse.pickgit.unit.comment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.comment.domain.Comments;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.comment.CommentNotFoundException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class CommentsTest {

    @DisplayName("댓글을 삭제한다.")
    @Test
    void delete_ValidComment_Success() {
        // given
        User user = UserFactory.user(1L, "dani");

        Comment comment1 = new Comment(1L, "comment1", user, null);
        Comment comment2 = new Comment(2L, "comment2", user, null);

        Comments comments = new Comments();
        comments.add(comment1);
        comments.add(comment2);

        Post post = Post.builder()
            .content("this is content")
            .comments(comments.getComments())
            .author(user)
            .build();

        // when
        comments.delete(post, user, comment1);

        // then
        assertThat(comments.getComments())
            .hasSize(1)
            .containsExactly(comment2);
    }

    @DisplayName("존재하지 않는 댓글은 삭제할 수 없다. - 400 예외")
    @Test
    void void_InvalidComment_400Exception() {
        // given
        User user = UserFactory.user(1L, "dani");

        Comment comment1 = new Comment(1L, "comment1", user, null);
        Comment comment2 = new Comment(2L, "comment2", user, null);

        Comments comments = new Comments();
        comments.add(comment1);

        Post post = Post.builder()
            .content("this is content")
            .comments(comments.getComments())
            .author(user)
            .build();

        // when
        assertThatThrownBy(() -> {
            comments.delete(post, user, comment2);
        }).isInstanceOf(CommentNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0008")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("해당 댓글이 존재하지 않습니다.");
    }
}
