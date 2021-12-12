package com.woowacourse.pickgit.config;

import com.woowacourse.pickgit.common.file_validator.FileValidator;
import com.woowacourse.pickgit.common.file_validator.ImageFileValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }

    @Bean
    public List<FileValidator> fileValidators() {
        return List.of(
                new ImageFileValidator()
        );
    }
}
