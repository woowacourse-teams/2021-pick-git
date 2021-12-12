package com.woowacourse.s3_proxy.config;

import com.woowacourse.s3_proxy.common.file_validator.FileValidator;
import com.woowacourse.s3_proxy.common.file_validator.ImageFileValidator;
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
