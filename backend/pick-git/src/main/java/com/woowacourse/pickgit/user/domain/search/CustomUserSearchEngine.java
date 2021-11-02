package com.woowacourse.pickgit.user.domain.search;

import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomUserSearchEngine {

    List<User> searchByUsernameLike(String username, Pageable pageable);
}
