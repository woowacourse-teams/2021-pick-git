package com.woowacourse.pickgit.tag;

import com.woowacourse.pickgit.common.mockapi.MockTagApiRequester;
import com.woowacourse.pickgit.tag.infrastructure.PlatformApiRequester;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestTagConfiguration {

    @Bean
    public PlatformApiRequester platformApiRequester() {
        return new MockTagApiRequester();
    }
}
