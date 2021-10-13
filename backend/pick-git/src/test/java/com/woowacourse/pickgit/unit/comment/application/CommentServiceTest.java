package com.woowacourse.pickgit.unit.comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.comment.domain.CommentRepository;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.post.CommentFormatException;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @DisplayName("게시물에 댓글을 정상 등록한다.")
    @Test
    void addComment_ValidContent_Success() {
        //given
        String content = "this is content";
        User user = UserFactory.user(1L, "testUser");
        Post post = Post.builder()
            .id(1L)
            .author(user)
            .build();

        Comment comment = new Comment(1L, content, user, post);

        given(postRepository.findById(anyLong()))
            .willReturn(Optional.of(post));
        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(user));
        given(commentRepository.save(any(Comment.class)))
            .willReturn(comment);

        CommentRequestDto commentRequestDto =
            new CommentRequestDto(user.getName(), content, post.getId());

        //when
        CommentResponseDto commentResponseDto = commentService.addComment(commentRequestDto);

        //then
        assertThat(commentResponseDto.getAuthorName()).isEqualTo(user.getName());
        assertThat(commentResponseDto.getContent()).isEqualTo(content);

        verify(postRepository, times(1))
            .findById(anyLong());
        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
        verify(commentRepository, times(1))
            .save(any(Comment.class));
    }

    @DisplayName("게시물에 빈 댓글을 등록할 수 없다.")
    @Test
    void addComment_EmptyContent_ExceptionThrown() {
        User user = UserFactory.user(1L, "testUser");
        Post post = Post.builder()
            .id(1L)
            .author(user)
            .build();

        given(userRepository.findByBasicProfile_Name(user.getName()))
            .willReturn(Optional.of(user));
        given(postRepository.findById(post.getId()))
            .willReturn(Optional.of(post));

        CommentRequestDto commentRequestDto =
            new CommentRequestDto(user.getName(), "", post.getId());

        // then
        assertThatCode(() -> commentService.addComment(commentRequestDto))
            .isInstanceOf(CommentFormatException.class)
            .extracting("errorCode")
            .isEqualTo("F0002");

        verify(userRepository, times(1))
            .findByBasicProfile_Name(user.getName());
        verify(postRepository, times(1))
            .findById(post.getId());
    }

    @DisplayName("존재하지 않는 사용자는 댓글을 등록할 수 없다.")
    @Test
    void addComment_InvalidUser_ExceptionOccur() {
        //given
        CommentRequestDto commentRequestDto =
            new CommentRequestDto("invalidUser", "comment_content", 1L);

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willThrow(new UserNotFoundException());

        //when then
        assertThatCode(() -> commentService.addComment(commentRequestDto))
            .isInstanceOf(UserNotFoundException.class)
            .extracting("errorCode")
            .isEqualTo("U0001");

        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
    }

    @DisplayName("존재하지 않는 게시물에는 댓글을 등록할 수 없다.")
    @Test
    void addComment_InvalidPost_ExceptionOccur() {
        //given
        CommentRequestDto commentRequestDto =
            new CommentRequestDto("testUser", "comment_content", 1L);

        given(userRepository.findByBasicProfile_Name(anyString()))
            .willReturn(Optional.of(UserFactory.user()));
        given(postRepository.findById(anyLong()))
            .willThrow(new PostNotFoundException());

        //when then
        assertThatCode(() -> commentService.addComment(commentRequestDto))
            .isInstanceOf(PostNotFoundException.class)
            .extracting("errorCode")
            .isEqualTo("P0002");

        verify(userRepository, times(1))
            .findByBasicProfile_Name(anyString());
        verify(postRepository, times(1))
            .findById(anyLong());
    }
}
