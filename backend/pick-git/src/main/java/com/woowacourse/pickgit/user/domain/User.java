package com.woowacourse.pickgit.user.domain;

import com.woowacourse.pickgit.post.domain.Posts;
import com.woowacourse.pickgit.user.domain.follow.Followers;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.statistics.UserStatistics;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private BasicProfile basicProfile;

    @Embedded
    private Followers followers;

    @Embedded
    private Followers followings;

    @Embedded
    private Posts posts;

    @Transient
    private UserStatistics statistics;

    public User() {
    }
}
