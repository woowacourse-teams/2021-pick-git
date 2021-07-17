package com.woowacourse.pickgit.post.domain;

import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByUser(User user);

    @Query("select p from Post p left join fetch p.user order by p.createdAt desc")
    List<Post> findAllPosts(Pageable pageable);

    @Query("select p from Post p where p.user = :user "
        + "order by p.createdAt desc")
    List<Post> findAllPostsByUser(@Param("user") User user, Pageable pageable);
}
