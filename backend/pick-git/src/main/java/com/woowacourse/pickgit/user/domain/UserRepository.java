package com.woowacourse.pickgit.user.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByBasicProfile_Name(String name);

    @Query("select u from User u where u.basicProfile.name like %:username%")
    List<User> searchByUsernameLike(@Param("username") String username, Pageable pageable);
}
