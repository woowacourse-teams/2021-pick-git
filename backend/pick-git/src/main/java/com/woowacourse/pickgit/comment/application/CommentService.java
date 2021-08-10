package com.woowacourse.pickgit.comment.application;

import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentService(UserRepository userRepository,
        PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public CommentResponseDto addComment(CommentRequestDto commentRequestDto) {
        String userName = commentRequestDto.getUserName();
        Long postId = commentRequestDto.getPostId();
        String content = commentRequestDto.getContent();

        User user = findUserByName(userName);
        Post post = findPostById(postId);

        Comment comment = new Comment(content, user);
        post.addComment(comment);

        return createCommentResponseDto(comment);
    }

    private CommentResponseDto createCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
            .id(comment.getId())
            .profileImageUrl(comment.getProfileImageUrl())
            .authorName(comment.getAuthorName())
            .content(comment.getContent())
            .liked(false)
            .build();
    }

    private Post findPostById(Long id) {
        return postRepository.findById(id)
            .orElseThrow(PostNotFoundException::new);
    }

    private User findUserByName(String username) {
        return userRepository
            .findByBasicProfile_Name(username)
            .orElseThrow(UserNotFoundException::new);
    }

}
