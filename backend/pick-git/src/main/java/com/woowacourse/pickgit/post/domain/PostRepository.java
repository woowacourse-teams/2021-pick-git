package com.woowacourse.pickgit.post.domain;

import com.woowacourse.pickgit.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByUser(User user);
}
