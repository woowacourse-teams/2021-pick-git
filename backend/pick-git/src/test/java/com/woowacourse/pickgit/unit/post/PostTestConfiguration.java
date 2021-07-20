package com.woowacourse.pickgit.unit.post;

import com.woowacourse.pickgit.common.mockapi.MockRepositoryApiRequester;
import com.woowacourse.pickgit.post.infrastructure.PlatformRepositoryApiRequester;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class PostTestConfiguration {

    @Bean
    public PlatformRepositoryApiRequester platformRepositoryApiRequester() {
        return new MockRepositoryApiRequester();
    }
}
