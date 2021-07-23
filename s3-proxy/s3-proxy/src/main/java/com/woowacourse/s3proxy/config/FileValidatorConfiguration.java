package com.woowacourse.s3proxy.config;

import com.woowacourse.s3proxy.common.filevalidator.FileValidator;
import com.woowacourse.s3proxy.common.filevalidator.ImageFileValidator;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileValidatorConfiguration {

    @Bean
    public List<FileValidator> fileValidator() {
        return List.of(
            new ImageFileValidator()
        );
    }
}
