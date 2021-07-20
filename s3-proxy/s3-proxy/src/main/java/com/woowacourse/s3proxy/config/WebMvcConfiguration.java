package com.woowacourse.s3proxy.config;

import com.woowacourse.s3proxy.common.fileValidator.FileValidator;
import com.woowacourse.s3proxy.common.fileValidator.ImageFileValidator;
import com.woowacourse.s3proxy.config.resolver.ExtensionValidArgumentResolver;
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
