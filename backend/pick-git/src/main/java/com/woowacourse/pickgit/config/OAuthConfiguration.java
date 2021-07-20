package com.woowacourse.pickgit.config;

import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.presentation.AuthenticationPrincipalArgumentResolver;
import com.woowacourse.pickgit.authentication.presentation.interceptor.AuthenticationInterceptor;
import com.woowacourse.pickgit.authentication.presentation.interceptor.IgnoreAuthenticationInterceptor;
import com.woowacourse.pickgit.authentication.presentation.interceptor.PathMatchInterceptor;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OAuthConfiguration implements WebMvcConfigurer {

    private final OAuthService oAuthService;

    public OAuthConfiguration(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor(oAuthService);
    }

    @Bean
    public IgnoreAuthenticationInterceptor ignoreAuthenticationInterceptor() {
        return new IgnoreAuthenticationInterceptor(oAuthService);
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
        HandlerInterceptor authenticationInterceptor = new PathMatchInterceptor(authenticationInterceptor())
            .addPathPatterns("/api/posts/me", HttpMethod.GET)
            .addPathPatterns("/api/github/*/repositories", HttpMethod.GET)
            .addPathPatterns("/api/github/repositories/*/tags/languages", HttpMethod.GET)
            .addPathPatterns("/api/posts", HttpMethod.POST)
            .addPathPatterns("/api/posts/*/likes", HttpMethod.POST, HttpMethod.DELETE)
            .addPathPatterns("/api/posts/*/comments", HttpMethod.POST)
            .addPathPatterns("/api/profiles/me", HttpMethod.GET)
            .addPathPatterns("/api/profiles/*/followings", HttpMethod.POST, HttpMethod.DELETE);

        HandlerInterceptor ignoreAuthenticationInterceptor = new PathMatchInterceptor(ignoreAuthenticationInterceptor())
            .addPathPatterns("/api/profiles/*", HttpMethod.GET)
            .addPathPatterns("/api/posts/*", HttpMethod.GET)
            .excludePatterns("/api/profiles/*/followings", HttpMethod.POST, HttpMethod.DELETE)
            .excludePatterns("/api/profiles/me", HttpMethod.GET)
            .excludePatterns("/api/posts/me", HttpMethod.GET);

        registry.addInterceptor(authenticationInterceptor)
            .addPathPatterns("/**");

        registry.addInterceptor(ignoreAuthenticationInterceptor)
            .addPathPatterns("/**");
    }
}
