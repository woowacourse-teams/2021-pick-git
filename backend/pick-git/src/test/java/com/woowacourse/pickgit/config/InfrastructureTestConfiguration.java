package com.woowacourse.pickgit.config;

import com.woowacourse.pickgit.common.mockapi.MockApiRequester;
import com.woowacourse.pickgit.common.mockapi.MockPickGitStorage;
import com.woowacourse.pickgit.common.mockapi.MockRepositoryApiRequester;
import com.woowacourse.pickgit.common.mockapi.MockTagApiRequester;
import com.woowacourse.pickgit.post.domain.PickGitStorage;
import com.woowacourse.pickgit.post.infrastructure.PlatformRepositoryApiRequester;
import com.woowacourse.pickgit.tag.infrastructure.PlatformTagApiRequester;
import com.woowacourse.pickgit.user.infrastructure.requester.PlatformApiRequester;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class InfrastructureTestConfiguration {

    @Bean
    public PlatformRepositoryApiRequester platformRepositoryApiRequester() {
        return new MockRepositoryApiRequester();
    }

    @Bean
    public PlatformTagApiRequester platformTagApiRequester() {
        return new MockTagApiRequester();
    }

    @Bean
    public PlatformApiRequester platformApiRequester() {
        return new MockApiRequester();
    }

    @Bean
    public PickGitStorage pickGitStorage() {
        return new MockPickGitStorage();
    }
}
