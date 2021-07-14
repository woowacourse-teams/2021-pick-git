package com.woowacourse.pickgit.post;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.post.infrastructure.MockRepositoryApiRequester;
import com.woowacourse.pickgit.post.infrastructure.PlatformRepositoryApiRequester;
import com.woowacourse.pickgit.post.presentation.PickGitStorage;
import java.io.File;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class PostTestConfiguration {

    @Bean
    public PickGitStorage pickGitStorage() {
        return (files, userName) -> files.stream()
            .map(File::getName)
            .collect(toList());
    }

    @Bean
    public PlatformRepositoryApiRequester platformRepositoryApiRequester() {
        return new MockRepositoryApiRequester();
    }
}
