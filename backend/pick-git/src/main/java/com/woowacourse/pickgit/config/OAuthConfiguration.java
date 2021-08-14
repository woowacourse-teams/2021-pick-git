package com.woowacourse.pickgit.config;

import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.presentation.AuthenticationPrincipalArgumentResolver;
import com.woowacourse.pickgit.authentication.presentation.interceptor.AuthenticationInterceptor;
import com.woowacourse.pickgit.authentication.presentation.interceptor.IgnoreAuthenticationInterceptor;
import com.woowacourse.pickgit.authentication.presentation.interceptor.PathMatchInterceptor;
import com.woowacourse.pickgit.config.auth_interceptor_register.AutoAuthorizationInterceptorRegister;
import com.woowacourse.pickgit.config.auth_interceptor_register.UriParser;
import com.woowacourse.pickgit.config.auth_interceptor_register.register_type.AuthenticateStorageForRegisterType;
import com.woowacourse.pickgit.config.auth_interceptor_register.register_type.IgnoreAuthenticateStorageForRegisterType;
import com.woowacourse.pickgit.config.auth_interceptor_register.register_type.StorageForRegisterType;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.ControllerScanner;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.ForGuestScanner;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.ForLoginUserScanner;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.package_scanner.PackageScanner;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.package_scanner.SourceVisitor;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OAuthConfiguration implements WebMvcConfigurer {

    private final OAuthService oAuthService;
    private String PACKAGE;

    public OAuthConfiguration(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor(oAuthService);
    }

    public IgnoreAuthenticationInterceptor ignoreAuthenticationInterceptor() {
        return new IgnoreAuthenticationInterceptor(oAuthService);
    }

    public AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver() {
        return new AuthenticationPrincipalArgumentResolver(oAuthService);
    }

    public List<StorageForRegisterType> getStorageForRegisterTypes() {
        return List.of(
            new AuthenticateStorageForRegisterType(),
            new IgnoreAuthenticateStorageForRegisterType()
        );
    }

    public List<String> parseClassesNames() {
        PACKAGE = "com.woowacourse.pickgit";

        PackageScanner packageScanner = new PackageScanner(PACKAGE, new SourceVisitor(PACKAGE));
        return packageScanner.getAllClassNames();
    }

    public UriParser getUriParser() {
        return new UriParser(
            new ControllerScanner(parseClassesNames()),
            new ForGuestScanner(),
            new ForLoginUserScanner()
        );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationPrincipalArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        PathMatchInterceptor authenticationInterceptor =
            new PathMatchInterceptor(authenticationInterceptor());
        PathMatchInterceptor ignoreAuthenticationInterceptor =
            new PathMatchInterceptor(ignoreAuthenticationInterceptor());

        AutoAuthorizationInterceptorRegister autoAuthorizationInterceptorRegister =
            AutoAuthorizationInterceptorRegister.builder()
            .storageForRegisterTypes(getStorageForRegisterTypes())
            .authenticationInterceptor(authenticationInterceptor)
            .ignoreAuthenticationInterceptor(ignoreAuthenticationInterceptor)
            .uriParser(getUriParser())
            .build();

        autoAuthorizationInterceptorRegister.execute();
        ignoreAuthenticationInterceptor
            .excludePatterns("/api/profiles/me", HttpMethod.GET);

        registry.addInterceptor(authenticationInterceptor)
            .addPathPatterns("/**");

        registry.addInterceptor(ignoreAuthenticationInterceptor)
            .addPathPatterns("/**");
    }
}
