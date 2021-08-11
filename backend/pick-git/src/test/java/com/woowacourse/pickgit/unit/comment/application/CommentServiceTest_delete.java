package com.woowacourse.pickgit.unit.comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.application.dto.request.CommentDeleteRequestDto;
import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.comment.domain.CommentRepository;
import com.woowacourse.pickgit.comment.domain.Comments;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.comment.CannotDeleteCommentException;
import com.woowacourse.pickgit.exception.comment.CommentNotFoundException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest_delete {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @DisplayName("내 게시물, 내 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByMeAndIsCommentedByMe_Success() {
        // given
        User me = UserFactory.user(1L, "dani");

        Comment comment1 = new Comment(1L, "comment1", me, null);
        Comment comment2 = new Comment(2L, "comment2", me, null);

        Comments comments = new Comments();
        comments.add(comment1);
        comments.add(comment2);

        Post postByMe = Post.builder()
            .id(1L)
            .comments(comments.getComments())
            .author(me)
            .build();

        given(postRepository.findById(1L))
            .willReturn(Optional.of(postByMe));
        given(userRepository.findByBasicProfile_Name("dani"))
            .willReturn(Optional.of(me));
        given(commentRepository.findById(1L))
            .willReturn(Optional.of(comment1));
        willDoNothing()
            .given(commentRepository)
            .delete(comment1);

        CommentDeleteRequestDto commentDeleteRequestDto = CommentDeleteRequestDto.builder()
            .username(me.getName())
            .postId(postByMe.getId())
            .commentId(comment1.getId())
            .build();

        // when
        commentService.delete(commentDeleteRequestDto);

        // then
        assertThat(comments.getComments())
            .hasSize(1)
            .containsExactly(comment2);

        verify(postRepository, times(1))
            .findById(1L);
        verify(userRepository, times(1))
            .findByBasicProfile_Name("dani");
        verify(commentRepository, times(1))
            .findById(1L);
        verify(commentRepository, times(1))
            .delete(comment1);
    }

    @DisplayName("내 게시물, 남 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByMeAndIsCommentedByOther_Success() {
        // given
        User me = UserFactory.user(1L, "dani");
        User other = UserFactory.user(2L, "dada");

        Comment comment1 = new Comment(1L, "comment1", other, null);
        Comment comment2 = new Comment(2L, "comment2", other, null);

        Comments comments = new Comments();
        comments.add(comment1);
        comments.add(comment2);

        Post postByMe = Post.builder()
            .id(1L)
            .comments(comments.getComments())
            .author(me)
            .build();

        given(postRepository.findById(1L))
            .willReturn(Optional.of(postByMe));
        given(userRepository.findByBasicProfile_Name("dani"))
            .willReturn(Optional.of(me));
        given(commentRepository.findById(2L))
            .willReturn(Optional.of(comment2));
        willDoNothing()
            .given(commentRepository)
            .delete(comment2);

        CommentDeleteRequestDto commentDeleteRequestDto = CommentDeleteRequestDto.builder()
            .username(me.getName())
            .postId(postByMe.getId())
            .commentId(comment2.getId())
            .build();

        // when
        commentService.delete(commentDeleteRequestDto);

        // then
        assertThat(comments.getComments())
            .hasSize(1)
            .containsExactly(comment1);

        verify(postRepository, times(1))
            .findById(1L);
        verify(userRepository, times(1))
            .findByBasicProfile_Name("dani");
        verify(commentRepository, times(1))
            .findById(2L);
        verify(commentRepository, times(1))
            .delete(comment2);
    }

    @DisplayName("남 게시물, 내 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByOtherAndIsCommentedByMe_Success() {
        // given
        User me = UserFactory.user(1L, "dani");
        User other = UserFactory.user(2L, "dada");

        Comment comment1 = new Comment(1L, "comment1", me, null);
        Comment comment2 = new Comment(2L, "comment2", me, null);

        Comments comments = new Comments();
        comments.add(comment1);
        comments.add(comment2);

        Post postByOther = Post.builder()
            .id(1L)
            .comments(comments.getComments())
            .author(other)
            .build();

        given(postRepository.findById(1L))
            .willReturn(Optional.of(postByOther));
        given(userRepository.findByBasicProfile_Name("dani"))
            .willReturn(Optional.of(me));
        given(commentRepository.findById(2L))
            .willReturn(Optional.of(comment2));
        willDoNothing()
            .given(commentRepository)
            .delete(comment2);

        CommentDeleteRequestDto commentDeleteRequestDto = CommentDeleteRequestDto.builder()
            .username(me.getName())
            .postId(postByOther.getId())
            .commentId(comment2.getId())
            .build();

        // when
        commentService.delete(commentDeleteRequestDto);

        // then
        assertThat(comments.getComments())
            .hasSize(1)
            .containsExactly(comment1);

        verify(postRepository, times(1))
            .findById(1L);
        verify(userRepository, times(1))
            .findByBasicProfile_Name("dani");
        verify(commentRepository, times(1))
            .findById(2L);
        verify(commentRepository, times(1))
            .delete(comment2);
    }

    @DisplayName("남 게시물, 남 댓글은 삭제할 수 없다. - 401 예외")
    @Test
    void delete_isWrittenByOtherAndIsCommentedByOther_401Exception() {
        // given
        User me = UserFactory.user(1L, "dani");
        User other = UserFactory.user(2L, "dada");

        Comment comment1 = new Comment(1L, "comment1", other, null);
        Comment comment2 = new Comment(2L, "comment2", other, null);

        Comments comments = new Comments();
        comments.add(comment1);
        comments.add(comment2);

        Post postByOther = Post.builder()
            .id(1L)
            .comments(comments.getComments())
            .author(other)
            .build();

        given(postRepository.findById(1L))
            .willReturn(Optional.of(postByOther));
        given(userRepository.findByBasicProfile_Name("dani"))
            .willReturn(Optional.of(me));
        given(commentRepository.findById(2L))
            .willReturn(Optional.of(comment2));

        CommentDeleteRequestDto commentDeleteRequestDto = CommentDeleteRequestDto.builder()
            .username(me.getName())
            .postId(postByOther.getId())
            .commentId(comment2.getId())
            .build();

        // when
        assertThatThrownBy(() -> {
            commentService.delete(commentDeleteRequestDto);
        }).isInstanceOf(CannotDeleteCommentException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0007")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("남 게시물, 남 댓글은 삭제할 수 없습니다.");

        // then
        verify(postRepository, times(1))
            .findById(1L);
        verify(userRepository, times(1))
            .findByBasicProfile_Name("dani");
        verify(commentRepository, times(1))
            .findById(2L);
    }

    @DisplayName("존재하지 않는 댓글은 삭제할 수 없다. - 400 예외")
    @Test
    void delete_invalidComment_400Exception() {
        // given
        User me = UserFactory.user(1L, "dani");
        User other = UserFactory.user(2L, "dada");

        Comment comment1 = new Comment(1L, "comment1", me, null);
        Comment comment2 = new Comment(2L, "comment2", me, null);

        Comments comments = new Comments();
        comments.add(comment1);
        comments.add(comment2);

        Post postByOther = Post.builder()
            .id(1L)
            .comments(comments.getComments())
            .author(other)
            .build();

        given(postRepository.findById(1L))
            .willReturn(Optional.of(postByOther));
        given(userRepository.findByBasicProfile_Name("dani"))
            .willReturn(Optional.of(me));
        given(commentRepository.findById(3L))
            .willThrow(CommentNotFoundException.class);

        CommentDeleteRequestDto commentDeleteRequestDto = CommentDeleteRequestDto.builder()
            .username(me.getName())
            .postId(postByOther.getId())
            .commentId(3L)
            .build();

        // when
        assertThatThrownBy(() -> {
            commentService.delete(commentDeleteRequestDto);
        }).isInstanceOf(CommentNotFoundException.class);

        // then
        verify(postRepository, times(1))
            .findById(1L);
        verify(userRepository, times(1))
            .findByBasicProfile_Name("dani");
        verify(commentRepository, times(1))
            .findById(3L);
    }
}
