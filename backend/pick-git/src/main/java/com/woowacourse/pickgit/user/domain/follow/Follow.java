package com.woowacourse.pickgit.user.domain.follow;

import com.woowacourse.pickgit.user.domain.User;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "SOURCE_ID")
    private User source;
    @ManyToOne
    @JoinColumn(name = "TARGET_ID")
    private User target;

    protected Follow() {
    }

}
