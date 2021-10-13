package com.woowacourse.pickgit.user.domain.search;

import com.woowacourse.pickgit.user.domain.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserSearchEngine extends ElasticsearchRepository<User, Long>, CustomUserSearchEngine {
}
