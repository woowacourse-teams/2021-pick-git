package com.woowacourse.pickgit.post.application;

import com.woowacourse.pickgit.post.application.dto.LikeRequestDto;
import com.woowacourse.pickgit.post.application.dto.LikeResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.post.domain.like.Like;
import com.woowacourse.pickgit.post.domain.like.LikeRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(LikeRepository likeRepository,
        PostRepository postRepository,
        UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public LikeResponseDto addLike(LikeRequestDto likeRequestDto) {
        Post post = postRepository.findById(likeRequestDto.getPostId())
            .orElseThrow(() -> new IllegalArgumentException("해당하는 게시물이 없습니다."));
        User user = userRepository.findById(1L)
            .orElseThrow(() -> new IllegalArgumentException("해당하는 사용자가 없습니다."));

        likeRepository.save(new Like(post, user));
        long likesCount = likeRepository.countByPostId(likeRequestDto.getPostId());

        return new LikeResponseDto(likesCount, true);
    }

    public LikeResponseDto deleteLike(LikeRequestDto likeRequestDto) {
        Post post = postRepository.findById(likeRequestDto.getPostId())
            .orElseThrow(() -> new IllegalArgumentException("해당하는 게시물이 없습니다."));
        User user = userRepository.findById(1L)
            .orElseThrow(() -> new IllegalArgumentException("해당하는 사용자가 없습니다."));

        likeRepository.delete(new Like(post, user));
        long likesCount = likeRepository.countByPostId(likeRequestDto.getPostId());

        return new LikeResponseDto(likesCount, false);
    }
}
