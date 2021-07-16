package com.woowacourse.pickgit.config;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.post.presentation.PickGitStorage;
import java.io.File;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class PostTestConfiguration {

    @Bean
    @Profile("test")
    public PickGitStorage pickGitStorage() {
        return (files, userName) -> files.stream()
            .map(File::getName)
            .collect(toList());
    }
}
