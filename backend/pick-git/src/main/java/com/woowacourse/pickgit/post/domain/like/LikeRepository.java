package com.woowacourse.pickgit.post.domain.like;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    long countByPostId(Long postId);
}
