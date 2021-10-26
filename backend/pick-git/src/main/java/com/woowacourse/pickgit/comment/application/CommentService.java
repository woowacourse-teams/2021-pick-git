package com.woowacourse.pickgit.comment.application;

import com.woowacourse.pickgit.comment.application.dto.CommentDtoAssembler;
import com.woowacourse.pickgit.comment.application.dto.request.CommentDeleteRequestDto;
import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.request.QueryCommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.comment.domain.CommentRepository;
import com.woowacourse.pickgit.exception.comment.CannotDeleteCommentException;
import com.woowacourse.pickgit.exception.comment.CommentNotFoundException;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @CacheEvict(value = "homeFeed", allEntries = true)
    @Transactional
    public CommentResponseDto addComment(CommentRequestDto commentRequestDto) {
        String content = commentRequestDto.getContent();
        User user = findUserByName(commentRequestDto.getUserName());
        Post post = findPostById(commentRequestDto.getPostId());

        Comment comment = new Comment(content, user, post);
        Comment savedComment = commentRepository.save(comment);

        return CommentDtoAssembler.commentResponseDto(savedComment);
    }

    public List<CommentResponseDto> queryComments(QueryCommentRequestDto queryCommentRequestDto) {
        Long postId = queryCommentRequestDto.getPostId();
        Pageable pageable = queryCommentRequestDto.getPageable();

        List<Comment> comments = commentRepository.findCommentsByPost_Id(postId, pageable);

        return CommentDtoAssembler.commentResponseDtos(comments);
    }

    @CacheEvict(value = "homeFeed", allEntries = true)
    @Transactional
    public void delete(CommentDeleteRequestDto commentDeleteRequestDto) {
        Post post = findPostById(commentDeleteRequestDto.getPostId());
        User user = findUserByName(commentDeleteRequestDto.getUsername());
        Comment comment = findCommentById(commentDeleteRequestDto.getCommentId());

        if (post.isNotWrittenBy(user) && comment.isNotCommentedBy(user)) {
            throw new CannotDeleteCommentException();
        }

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
