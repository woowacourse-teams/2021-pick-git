package com.woowacourse.pickgit.unit.tag;

import com.woowacourse.pickgit.common.mockapi.MockTagApiRequester;
import com.woowacourse.pickgit.tag.infrastructure.PlatformTagApiRequester;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestTagConfiguration {

    @Bean
    public PlatformTagApiRequester platformTagApiRequester() {
        return new MockTagApiRequester();
    }
}
