package com.woowacourse.pickgit.config;

import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.presentation.AuthenticationPrincipalArgumentResolver;
import com.woowacourse.pickgit.authentication.presentation.interceptor.AuthenticationInterceptor;
import com.woowacourse.pickgit.authentication.presentation.interceptor.IgnoreAuthenticationInterceptor;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OAuthConfiguration implements WebMvcConfigurer {

    private OAuthService oAuthService;

    public OAuthConfiguration(
        OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor(oAuthService);
    }

    @Bean
    public IgnoreAuthenticationInterceptor ignoreAuthenticationInterceptor() {
        return new IgnoreAuthenticationInterceptor();
    }

    @Bean
    public AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver() {
        return new AuthenticationPrincipalArgumentResolver(oAuthService);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationPrincipalArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor())
            .addPathPatterns("/api/profiles/me")
            .addPathPatterns("/api/posts/me")
            .excludePathPatterns("/api/authorization/github")
            .excludePathPatterns("/api/afterlogin");

        registry.addInterceptor(ignoreAuthenticationInterceptor())
            .addPathPatterns("/api/profiles/*")
            .addPathPatterns("/api/posts/*");
    }
}
