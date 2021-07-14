package com.woowacourse.pickgit.post.application;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.post.domain.comment.CommentFormatException;
import com.woowacourse.pickgit.post.domain.comment.Comments;
import com.woowacourse.pickgit.post.presentation.PickGitStorage;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import java.io.File;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PostServiceIntegrationTest {

    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private PickGitStorage pickGitStorage =  (files, userName) -> files.stream()
            .map(File::getName)
            .collect(toList());

    private Post post;

    private User user;

    @BeforeEach
    void setUp() {
        postService = new PostService(userRepository, postRepository, pickGitStorage);
        post = new Post(null, null, null, null, null, new Comments(), new ArrayList<>(), null);
        user =
            new User(new BasicProfile("kevin", "a.jpg", "a"),
                new GithubProfile("github.com", "a", "a", "a", "a"));
        postRepository.save(post);
        userRepository.save(user);
    }

    @DisplayName("게시물에 댓글을 정상 등록한다.")
    @Test
    void addComment_ValidContent_Success() {
        CommentRequestDto commentRequestDto =
            new CommentRequestDto("kevin", "test comment", post.getId());

        CommentResponseDto commentResponseDto = postService.addComment(commentRequestDto);

        assertThat(commentResponseDto.getAuthorName()).isEqualTo("kevin");
        assertThat(commentResponseDto.getContent()).isEqualTo("test comment");
    }

    @DisplayName("게시물에 빈 댓글은 등록할 수 없다.")
    @Test
    void addComment_InvalidContent_ExceptionThrown() {
        CommentRequestDto commentRequestDto =
            new CommentRequestDto("kevin", "", post.getId());

        assertThatCode(() -> postService.addComment(commentRequestDto))
            .isInstanceOf(CommentFormatException.class)
            .hasMessage("F0002");
    }
}
