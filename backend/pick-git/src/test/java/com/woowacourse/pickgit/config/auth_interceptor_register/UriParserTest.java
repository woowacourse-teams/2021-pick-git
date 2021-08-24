package com.woowacourse.pickgit.config.auth_interceptor_register;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.config.auth_interceptor_register.register_type.RegisterType;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.ControllerScanner;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.ForGuestScanner;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.ForLoginUserScanner;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.data_structure.MergedInterceptorParameterByMethod;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.test_classes.ClassOne;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.test_classes.ClassThree;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.test_classes.ClassTwo;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

class UriParserTest {

    @DisplayName("인터셉터에 등록할 url과 method를 반환한다..")
    @Test
    void getPreparedControllerMethod_returnUrlAndMethods_Success() {
        List<String> classNames = List.of(
            ClassOne.class.getCanonicalName(),
            ClassTwo.class.getCanonicalName(),
            ClassThree.class.getCanonicalName()
        );

        UriParser uriParser = new UriParser(
            new ControllerScanner(classNames),
            new ForGuestScanner(),
            new ForLoginUserScanner()
        );

        List<MergedInterceptorParameterByMethod> actual = uriParser
            .getMergedInterceptorParameterByMethod();
        actual.sort(comparing(MergedInterceptorParameterByMethod::getUrls)
            .thenComparing(MergedInterceptorParameterByMethod::getHttpMethod));

        List<MergedInterceptorParameterByMethod> expected = expected();
        expected.sort(comparing(MergedInterceptorParameterByMethod::getUrls)
            .thenComparing(MergedInterceptorParameterByMethod::getHttpMethod));

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private List<MergedInterceptorParameterByMethod> expected() {
        return new ArrayList<>(List.of(
            new MergedInterceptorParameterByMethod(
                "/api/test1", HttpMethod.GET, RegisterType.AUTHENTICATE
            ),
            new MergedInterceptorParameterByMethod(
                "/api/test2/*/test", HttpMethod.POST, RegisterType.AUTHENTICATE
            ),
            new MergedInterceptorParameterByMethod(
                "/api/test3", HttpMethod.PUT, RegisterType.IGNORE_AUTHENTICATE
            ),
            new MergedInterceptorParameterByMethod(
                "/api/test4/*", HttpMethod.DELETE, RegisterType.IGNORE_AUTHENTICATE
            ),
            new MergedInterceptorParameterByMethod(
                "/test9", HttpMethod.DELETE, RegisterType.AUTHENTICATE
            ),
            new MergedInterceptorParameterByMethod(
                "/test9", HttpMethod.PUT, RegisterType.AUTHENTICATE
            )
        ));
    }
}
