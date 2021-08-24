package com.woowacourse.pickgit.comment.domain;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findCommentsByPost_Id(Long postId, Pageable pageable);
}
