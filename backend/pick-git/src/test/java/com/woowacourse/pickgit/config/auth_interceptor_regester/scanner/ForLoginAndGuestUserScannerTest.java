package com.woowacourse.pickgit.config.auth_interceptor_regester.scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes.ClassOne;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes.ClassThree;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes.ClassTwo;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ForLoginAndGuestUserScannerTest {

    @DisplayName("forGuest 어노테이션이 있는 method를 추출한다.")
    @ParameterizedTest
    @MethodSource("getParametersForParseMethods")
    void parseMethods(Class<?> classToken, int size) {
        ForGuestScanner forGuestScanner = new ForGuestScanner();

        List<Method> methods = forGuestScanner.parseMethods(classToken);
        assertThat(methods).hasSize(size);
    }

    private static Stream<Arguments> getParametersForParseMethods() {
        return Stream.of(
            Arguments.of(ClassOne.class, 2),
            Arguments.of(ClassThree.class, 2),
            Arguments.of(ClassTwo.class, 0)
        );
    }
}