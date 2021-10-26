package com.woowacourse.pickgit.integration.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.post.CommentFormatException;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.integration.IntegrationTest;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

public class CommentServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("게시물에 댓글을 정상 등록한다.")
    @Test
    void addComment_ValidContent_Success() {
        User testUser = UserFactory.user("testUser");
        User savedTestUser = userRepository.save(testUser);

        User kevin = UserFactory.user("kevin");
        User savedKevin = userRepository.save(kevin);

        Post post = Post.builder()
            .content("testContent")
            .githubRepoUrl("https://github.com/bperhaps")
            .author(savedTestUser)
            .build();
        Post savedPost = postRepository.save(post);

        // when
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
            .userName("kevin")
            .content("test comment")
            .postId(savedPost.getId())
            .build();

        CommentResponseDto commentResponseDto = commentService.addComment(commentRequestDto);

        // then
        assertThat(commentResponseDto.getAuthorName()).isEqualTo(savedKevin.getName());
        assertThat(commentResponseDto.getContent()).isEqualTo("test comment");
    }

    @DisplayName("Post에 빈 Comment은 등록할 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void addComment_InvalidContent_ExceptionThrown(String content) {
        User testUser = UserFactory.user("testUser");
        User savedTestUser = userRepository.save(testUser);

        User kevin = UserFactory.user("kevin");
        userRepository.save(kevin);

        Post post = Post.builder()
            .content("testContent")
            .githubRepoUrl("https://github.com/bperhaps")
            .author(savedTestUser)
            .build();
        Post savedPost = postRepository.save(post);

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
            .userName("kevin")
            .content(content)
            .postId(savedPost.getId())
            .build();

        // then
        assertThatCode(() -> commentService.addComment(commentRequestDto))
            .isInstanceOf(CommentFormatException.class)
            .extracting("errorCode")
            .isEqualTo("F0002");
    }

    @DisplayName("존재하지 않는 Post에 Comment를 등록할 수 없다.")
    @Test
    void addComment_PostNotFound_ExceptionThrown() {
        userRepository.save(UserFactory.user("kevin"));

        // when
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
            .userName("kevin")
            .content("content")
            .postId(-1L)
            .build();

        // then
        assertThatCode(() -> commentService.addComment(commentRequestDto))
            .isInstanceOf(PostNotFoundException.class)
            .extracting("errorCode")
            .isEqualTo("P0002");
    }

    @DisplayName("존재하지 않는 User는 Comment를 등록할 수 없다.")
    @Test
    void addComment_UserNotFound_ExceptionThrown() {
        // given
        Post post = Post.builder()
            .build();
        Post savedPost = postRepository.save(post);

        // when
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
            .userName("anonymous")
            .content("content")
            .postId(savedPost.getId())
            .build();

        // then
        assertThatCode(() -> commentService.addComment(commentRequestDto))
            .isInstanceOf(UserNotFoundException.class)
            .extracting("errorCode")
            .isEqualTo("U0001");
    }
}
