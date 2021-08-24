package com.woowacourse.pickgit.post.domain.repository;

import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
import java.util.Optional;
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

    @Query("select distinct p from Post p left join fetch p.likes.likes l left join fetch l.user where p.id = :postId")
    Optional<Post> findPostWithLikeUsers(@Param("postId") Long postId);

    @Query("select p from Post p join fetch p.user u "
        + "where u in "
        + "(select t from Follow f inner join f.target t on f.source = :user) "
        + "or u = :user "
        + "order by p .createdAt desc")
    List<Post> findAllAssociatedPostsByUser(@Param("user") User user, Pageable pageable);
}
