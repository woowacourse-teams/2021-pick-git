package com.woowacourse.s3_proxy.config;

import com.woowacourse.s3_proxy.common.file_validator.FileValidator;
import com.woowacourse.s3_proxy.common.file_validator.ImageFileValidator;
import com.woowacourse.s3_proxy.config.resolver.ExtensionValidArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ExtensionValidArgumentResolver(createFileValidators()));
    }

    public List<FileValidator> createFileValidators() {
        return List.of(
            new ImageFileValidator()
        );
    }
}
