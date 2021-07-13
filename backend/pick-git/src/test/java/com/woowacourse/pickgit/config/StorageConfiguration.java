package com.woowacourse.pickgit.config;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.post.presentation.PickGitStorage;
import java.io.File;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class StorageConfiguration {

    @Bean
    public PickGitStorage pickGitStorage() {
        return (files, userName) -> files.stream()
            .map(File::getName)
            .collect(toList());
    }

}
