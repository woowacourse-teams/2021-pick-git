package com.woowacourse.pickgit.integration.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.application.dto.request.CommentDeleteRequestDto;
import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.comment.domain.CommentRepository;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.comment.CannotDeleteCommentException;
import com.woowacourse.pickgit.exception.comment.CommentNotFoundException;
import com.woowacourse.pickgit.integration.IntegrationTest;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class CommentServiceIntegrationTest_delete extends IntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("내 게시물, 내 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByMeAndIsCommentedByMe_Success() {
        // given
        User me = UserFactory.user("dani");
        User savedMe = userRepository.save(me);

        Post postByMe = Post.builder()
            .content("hi")
            .githubRepoUrl("https://github.com/da-nyee")
            .author(savedMe)
            .build();
        Post savedPostByMe = postRepository.save(postByMe);

        CommentRequestDto commentRequestDto1 =
            createCommentRequestDto(savedMe.getName(), savedPostByMe.getId());
        CommentRequestDto commentRequestDto2 =
            createCommentRequestDto(savedMe.getName(), savedPostByMe.getId());

        CommentResponseDto commentResponseDto1 = commentService.addComment(commentRequestDto1);
        CommentResponseDto commentResponseDto2 = commentService.addComment(commentRequestDto2);

        CommentDeleteRequestDto commentDeleteRequestDto = createCommentDeleteRequestDto(
            savedMe.getName(),
            postByMe.getId(),
            commentResponseDto1.getId()
        );

        Comment responseComment = new Comment(
            commentResponseDto2.getId(),
            "this is content",
            savedMe,
            savedPostByMe
        );

        // when
        commentService.delete(commentDeleteRequestDto);

        // then
        assertThat(commentRepository.findAll())
            .hasSize(1)
            .containsExactly(responseComment);
    }

    @DisplayName("내 게시물, 남 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByMeAndIsCommentedByOther_Success() {
        // given
        User me = UserFactory.user("dani");
        User savedMe = userRepository.save(me);

        User other = UserFactory.user("dada");
        User savedOther = userRepository.save(other);

        Post postByMe = Post.builder()
            .content("hi")
            .githubRepoUrl("https://github.com/da-nyee")
            .author(savedMe)
            .build();
        Post savedPostByMe = postRepository.save(postByMe);

        CommentRequestDto commentRequestDto1 =
            createCommentRequestDto(savedOther.getName(), savedPostByMe.getId());
        CommentRequestDto commentRequestDto2 =
            createCommentRequestDto(savedOther.getName(), savedPostByMe.getId());

        CommentResponseDto commentResponseDto1 = commentService.addComment(commentRequestDto1);
        CommentResponseDto commentResponseDto2 = commentService.addComment(commentRequestDto2);

        CommentDeleteRequestDto commentDeleteRequestDto = createCommentDeleteRequestDto(
            savedMe.getName(),
            postByMe.getId(),
            commentResponseDto2.getId()
        );

        Comment responseComment = new Comment(
            commentResponseDto1.getId(),
            "this is content",
            savedOther,
            savedPostByMe
        );

        // when
        commentService.delete(commentDeleteRequestDto);

        // then
        assertThat(commentRepository.findAll())
            .hasSize(1)
            .containsExactly(responseComment);
    }

    @DisplayName("남 게시물, 내 댓글을 삭제한다.")
    @Test
    void delete_isWrittenByOtherAndIsCommentedByMe_Success() {
        // given
        User me = UserFactory.user("dani");
        User savedMe = userRepository.save(me);

        User other = UserFactory.user("dada");
        User savedOther = userRepository.save(other);

        Post postByOther = Post.builder()
            .content("hi")
            .githubRepoUrl("https://github.com/da-nyee")
            .author(savedOther)
            .build();
        Post savedPostByOther = postRepository.save(postByOther);

        CommentRequestDto commentRequestDto1 =
            createCommentRequestDto(savedMe.getName(), savedPostByOther.getId());
        CommentRequestDto commentRequestDto2 =
            createCommentRequestDto(savedMe.getName(), savedPostByOther.getId());

        CommentResponseDto commentResponseDto1 = commentService.addComment(commentRequestDto1);
        CommentResponseDto commentResponseDto2 = commentService.addComment(commentRequestDto2);

        CommentDeleteRequestDto commentDeleteRequestDto = createCommentDeleteRequestDto(
            savedMe.getName(),
            postByOther.getId(),
            commentResponseDto1.getId()
        );

        Comment responseComment = new Comment(
            commentResponseDto2.getId(),
            "this is content",
            savedMe,
            savedPostByOther
        );

        // when
        commentService.delete(commentDeleteRequestDto);

        // then
        assertThat(commentRepository.findAll())
            .hasSize(1)
            .containsExactly(responseComment);
    }

    @DisplayName("남 게시물, 남 댓글은 삭제할 수 없다. - 401 예외")
    @Test
    void delete_isWrittenByOtherAndIsCommentedByOther_401Exception() {
        // given
        User me = UserFactory.user("dani");
        User savedMe = userRepository.save(me);

        User other = UserFactory.user("dada");
        User savedOther = userRepository.save(other);

        Post postByOther = Post.builder()
            .content("hi")
            .githubRepoUrl("https://github.com/da-nyee")
            .author(savedOther)
            .build();
        Post savedPostByOther = postRepository.save(postByOther);

        CommentRequestDto commentRequestDto1 =
            createCommentRequestDto(savedOther.getName(), savedPostByOther.getId());
        CommentRequestDto commentRequestDto2 =
            createCommentRequestDto(savedOther.getName(), savedPostByOther.getId());

        commentService.addComment(commentRequestDto1);
        CommentResponseDto commentResponseDto2 = commentService.addComment(commentRequestDto2);

        CommentDeleteRequestDto commentDeleteRequestDto = createCommentDeleteRequestDto(
            savedMe.getName(),
            postByOther.getId(),
            commentResponseDto2.getId()
        );

        // when
        assertThatThrownBy(() -> {
            commentService.delete(commentDeleteRequestDto);
        }).isInstanceOf(CannotDeleteCommentException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0007")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
            .hasMessage("남 게시물, 남 댓글은 삭제할 수 없습니다.");
    }

    @DisplayName("존재하지 않는 댓글은 삭제할 수 없다. - 400 예외")
    @Test
    void delete_invalidComment_400Exception() {
        // given
        User me = UserFactory.user("dani");
        User savedMe = userRepository.save(me);

        Post postByMe = Post.builder()
            .content("hi")
            .githubRepoUrl("https://github.com/da-nyee")
            .author(savedMe)
            .build();
        Post savedPostByMe = postRepository.save(postByMe);

        CommentRequestDto commentRequestDto =
            createCommentRequestDto(savedMe.getName(), savedPostByMe.getId());

        commentService.addComment(commentRequestDto);

        CommentDeleteRequestDto commentDeleteRequestDto = createCommentDeleteRequestDto(
            savedMe.getName(),
            postByMe.getId(),
            100L
        );

        // when
        assertThatThrownBy(() -> {
            commentService.delete(commentDeleteRequestDto);
        }).isInstanceOf(CommentNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0008")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasMessage("해당 댓글이 존재하지 않습니다.");
    }

    private CommentRequestDto createCommentRequestDto(String username, Long postId) {
        return CommentRequestDto.builder()
            .userName(username)
            .content("this is comment")
            .postId(postId)
            .build();
    }

    private CommentDeleteRequestDto createCommentDeleteRequestDto(
        String username,
        Long postId,
        Long commentId
    ) {
        return CommentDeleteRequestDto.builder()
            .username(username)
            .postId(postId)
            .commentId(commentId)
            .build();
    }
}
