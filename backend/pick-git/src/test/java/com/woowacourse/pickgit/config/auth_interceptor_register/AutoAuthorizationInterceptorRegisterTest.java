package com.woowacourse.pickgit.config.auth_interceptor_register;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.authentication.presentation.interceptor.PathMatchInterceptor;
import com.woowacourse.pickgit.config.auth_interceptor_register.register_type.AuthenticateStorageForRegisterType;
import com.woowacourse.pickgit.config.auth_interceptor_register.register_type.IgnoreAuthenticateStorageForRegisterType;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.ControllerScanner;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.ForGuestScanner;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.ForLoginUserScanner;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.test_classes.ClassOne;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.test_classes.ClassThree;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.test_classes.ClassTwo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

class AutoAuthorizationInterceptorRegisterTest {

    @DisplayName("서로 매핑되는 Url, Method들을 반환한다.")
    @Test
    void execute_createUrlAndMethods_Success() {
        List<String> classNames = List.of(
            ClassOne.class.getCanonicalName(),
            ClassTwo.class.getCanonicalName(),
            ClassThree.class.getCanonicalName()
        );
        TestPathMatchInterceptor authenticationInterceptor = new TestPathMatchInterceptor(null);
        TestPathMatchInterceptor ignoreAuthenticationInterceptor =
            new TestPathMatchInterceptor(null);

        AutoAuthorizationInterceptorRegister autoAuthorizationInterceptorRegister =
            AutoAuthorizationInterceptorRegister.builder()
            .storageForRegisterTypes(
                List.of(new AuthenticateStorageForRegisterType(),
                    new IgnoreAuthenticateStorageForRegisterType()))
            .authenticationInterceptor(authenticationInterceptor)
            .ignoreAuthenticationInterceptor(ignoreAuthenticationInterceptor)
            .uriParser(new UriParser(
                new ControllerScanner(classNames),
                new ForGuestScanner(),
                new ForLoginUserScanner()))
            .build();

        autoAuthorizationInterceptorRegister.execute();

        List<Pair> authenticationActual = authenticationInterceptor.getIncludeRegistry();
        List<Pair> ignoreAuthenticationActual = ignoreAuthenticationInterceptor
            .getIncludeRegistry();

        List<Pair> authenticationExpected = authenticationExpected();
        List<Pair> ignoreAuthenticationExpected = ignoreAuthenticationExpected();

        authenticationActual.sort(comparing(Pair::getUri));
        ignoreAuthenticationActual.sort(comparing(Pair::getUri));
        authenticationExpected.sort(comparing(Pair::getUri));
        ignoreAuthenticationExpected.sort(comparing(Pair::getUri));

        assertThat(authenticationActual)
            .usingRecursiveComparison()
            .isEqualTo(authenticationExpected);

        assertThat(ignoreAuthenticationExpected)
            .usingRecursiveComparison()
            .isEqualTo(ignoreAuthenticationExpected);
    }

    private List<Pair> authenticationExpected() {
        return new ArrayList<>(List.of(
            new Pair("/api/test1", List.of(HttpMethod.GET)),
            new Pair("/api/test2/*/test", List.of(HttpMethod.POST)),
            new Pair("/test9", List.of(HttpMethod.DELETE, HttpMethod.PUT))
        ));
    }

    private List<Pair> ignoreAuthenticationExpected() {
        return new ArrayList<>(List.of(
            new Pair("/api/test3", List.of(HttpMethod.PUT)),
            new Pair("/api/test4/*", List.of(HttpMethod.DELETE))
        ));
    }

    private static class Pair {

        private final String uri;
        private final List<HttpMethod> methods;

        public Pair(String uri, List<HttpMethod> methods) {
            this.uri = uri;
            methods = new ArrayList<>(methods);
            methods.sort(comparing(HttpMethod::name));
            this.methods = methods;
        }

        public String getUri() {
            return uri;
        }

        public List<HttpMethod> getMethods() {
            return methods;
        }
    }

    private static class TestPathMatchInterceptor extends PathMatchInterceptor {

        private final HashMap<String, List<HttpMethod>> includeRegistry;

        public TestPathMatchInterceptor(
            HandlerInterceptor handlerInterceptor
        ) {
            super(handlerInterceptor);
            this.includeRegistry = new HashMap<>();
        }

        @Override
        public PathMatchInterceptor addPathPatterns(String pattern, List<HttpMethod> methods) {
            this.includeRegistry.putIfAbsent(pattern, methods);
            return this;
        }

        public List<Pair> getIncludeRegistry() {
            return includeRegistry.entrySet().stream()
                .map(entry -> new Pair(entry.getKey(), entry.getValue()))
                .collect(toList());
        }
    }
}
