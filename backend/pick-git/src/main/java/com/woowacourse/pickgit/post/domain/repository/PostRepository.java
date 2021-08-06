package com.woowacourse.pickgit.post.domain.repository;

import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p left join fetch p.user order by p.createdAt desc")
    List<Post> findAllPosts(Pageable pageable);

    @Query("select p from Post p where p.user = :user order by p.createdAt desc")
    List<Post> findAllPostsByUser(@Param("user") User user, Pageable pageable);

    @Query("select distinct p from PostTag pt inner join pt.post p where pt.tag.name in :tagName")
    List<Post> findAllPostsByTagNames(@Param("tagName") List<String> tagName, Pageable pageable);
}
