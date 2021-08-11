package com.woowacourse.pickgit.config.auth_interceptor_regester;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.ControllerScanner;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.ForGuestScanner;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.ForLoginUserScanner;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.RegisterType;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.data_structure.PreparedControllerMethod;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes.ClassOne;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes.ClassThree;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes.ClassTwo;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

class UriParserTest {

    @Test
    void getPreparedControllerMethod() {
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

        List<PreparedControllerMethod> actual = uriParser
            .getPreparedControllerMethod();
        actual.sort(comparing(PreparedControllerMethod::getUrls));

        List<PreparedControllerMethod> expected = expected();
        expected.sort(comparing(PreparedControllerMethod::getUrls));

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private List<PreparedControllerMethod> expected() {
        return new ArrayList<>(List.of(
            new PreparedControllerMethod(
                "/api/test1", HttpMethod.GET, RegisterType.AUTHENTICATE
            ),
            new PreparedControllerMethod(
                "/api/test2", HttpMethod.POST, RegisterType.AUTHENTICATE
            ),
            new PreparedControllerMethod(
                "/api/test3", HttpMethod.PUT, RegisterType.IGNORE_AUTHENTICATE
            ),
            new PreparedControllerMethod(
                "/api/test4", HttpMethod.DELETE, RegisterType.IGNORE_AUTHENTICATE
            ),
            new PreparedControllerMethod(
                "/test9", HttpMethod.DELETE, RegisterType.AUTHENTICATE
            )
        ));
    }
}