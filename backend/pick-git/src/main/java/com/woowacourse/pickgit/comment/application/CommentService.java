package com.woowacourse.pickgit.comment.application;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.comment.application.dto.request.CommentDeleteRequestDto;
import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.request.QueryCommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.comment.domain.CommentRepository;
import com.woowacourse.pickgit.exception.comment.CommentNotFoundException;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentService(
        CommentRepository commentRepository,
        UserRepository userRepository,
        PostRepository postRepository
    ) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public CommentResponseDto addComment(CommentRequestDto commentRequestDto) {
        String content = commentRequestDto.getContent();
        User user = findUserByName(commentRequestDto.getUserName());
        Post post = findPostById(commentRequestDto.getPostId());

        Comment comment = new Comment(content, user, post);
        Comment savedComment = commentRepository.save(comment);

        return createCommentResponseDto(savedComment);
    }

    public List<CommentResponseDto> queryComments(QueryCommentRequestDto queryCommentRequestDto) {
        Long postId = queryCommentRequestDto.getPostId();
        int page = queryCommentRequestDto.getPage();
        int limit = queryCommentRequestDto.getLimit();

        Pageable pageable = PageRequest.of(page, limit);

        List<Comment> comments = commentRepository.findCommentsByPost_Id(postId, pageable);

        return comments.stream()
            .map(this::createCommentResponseDto)
            .collect(toList());
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

    public void delete(CommentDeleteRequestDto commentDeleteRequestDto) {
        Post post = findPostById(commentDeleteRequestDto.getPostId());
        User user = findUserByName(commentDeleteRequestDto.getUsername());
        Comment comment = findCommentById(commentDeleteRequestDto.getCommentId());

        comment.deleteFrom(post, user);
        commentRepository.delete(comment);
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(PostNotFoundException::new);
    }

    private User findUserByName(String username) {
        return userRepository
            .findByBasicProfile_Name(username)
            .orElseThrow(UserNotFoundException::new);
    }

    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(CommentNotFoundException::new);
    }
}
