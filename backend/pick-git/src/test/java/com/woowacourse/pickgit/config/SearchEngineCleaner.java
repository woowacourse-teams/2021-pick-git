package com.woowacourse.pickgit.config;

import com.woowacourse.pickgit.user.domain.search.UserSearchEngine;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class SearchEngineCleaner implements InitializingBean {

    @Autowired
    private UserSearchEngine userSearchEngine;

    @Override
    public void afterPropertiesSet() {
        userSearchEngine.deleteAll();
    }
}
