package com.woowacourse.pickgit.unit.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.comment.Comment;
import com.woowacourse.pickgit.post.domain.comment.Comments;
import com.woowacourse.pickgit.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @DisplayName("User가 특정 Post에 Comment를 추가한다.")
    @Test
    void addComment_Valid_RegistrationSuccess() {
        // given
        Post post = new Post(null, null, null, null, null, new Comments(), new ArrayList<>(), null);
        Comment comment = new Comment("test comment.");
        User user = new User(null, null, null);

        // when
        user.addComment(post, comment);

        // then
        List<Comment> comments = post.getComments();
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getUser()).isSameAs(user);
    }
}
