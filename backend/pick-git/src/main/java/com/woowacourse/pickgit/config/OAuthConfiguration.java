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
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OAuthConfiguration implements WebMvcConfigurer {

    private static final String PACKAGE = "com.woowacourse.pickgit";

    private final OAuthService oAuthService;

    public OAuthConfiguration(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationPrincipalArgumentResolver());
    }

    private AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver() {
        return new AuthenticationPrincipalArgumentResolver(oAuthService);
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

    private AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor(oAuthService);
    }

    private IgnoreAuthenticationInterceptor ignoreAuthenticationInterceptor() {
        return new IgnoreAuthenticationInterceptor(oAuthService);
    }

    private List<StorageForRegisterType> getStorageForRegisterTypes() {
        return List.of(
            new AuthenticateStorageForRegisterType(),
            new IgnoreAuthenticateStorageForRegisterType()
        );
    }

    private UriParser getUriParser() {
        return new UriParser(
            new ControllerScanner(parseClassesNames()),
            new ForGuestScanner(),
            new ForLoginUserScanner()
        );
    }

    private List<String> parseClassesNames() {
        PackageScanner packageScanner = new PackageScanner(PACKAGE);
        return packageScanner.getAllClassNames();
    }
}
