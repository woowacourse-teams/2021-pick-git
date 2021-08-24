package com.woowacourse.pickgit.config.auth_interceptor_register.scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.test_classes.ClassOne;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.test_classes.inner.ClassFour;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

class HttpMethodMapperTest {

    @DisplayName("메서드에 매핑된 HttpMethod를 추출한다 - 성공")
    @ParameterizedTest
    @MethodSource("getParametersForFindHttpMethodByControllerMethod_Success")
    void findHttpMethodByControllerMethod_ExtractMappedHttpMethod_Success(
        Class<?> classToken,
        String methodName,
        HttpMethod expected
    ) throws NoSuchMethodException {
        Method method = classToken.getMethod(methodName);
        HttpMethod httpMethod = HttpMethodMapper.findHttpMethodByControllerMethod(method);

        assertThat(httpMethod).isEqualTo(expected);
    }

    private static Stream<Arguments> getParametersForFindHttpMethodByControllerMethod_Success() {
        return Stream.of(
            Arguments.of(ClassOne.class, "test1", HttpMethod.GET),
            Arguments.of(ClassOne.class, "test2", HttpMethod.POST),
            Arguments.of(ClassOne.class, "test3", HttpMethod.PUT),
            Arguments.of(ClassOne.class, "test4", HttpMethod.DELETE)
        );
    }

    @DisplayName("메서드에 매핑된 HttpMethod를 추출한다 - 실패")
    @Test
    void findHttpMethodByControllerMethod_ExtractMappedHttpMethod_Fail() throws NoSuchMethodException {
        Method method = ClassFour.class.getMethod("test1");

        assertThatThrownBy(() -> HttpMethodMapper.findHttpMethodByControllerMethod(method))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메서드에 매핑된 HttpMethodAnnotaion을 추출한다. - 성공")
    @ParameterizedTest
    @MethodSource("getParametersForFindAnnotatedHttpMethodAnnotation")
    void findAnnotatedHttpMethodAnnotation_ExtractMappedHttpMethodAnnotation_Success(
        Class<?> classToken,
        String methodName,
        Class<? extends Annotation> expected
    ) throws NoSuchMethodException {
        Method method = classToken.getMethod(methodName);
        Class<? extends Annotation> methodMapping = HttpMethodMapper
            .findAnnotatedHttpMethodAnnotation(method);

        assertThat(methodMapping).isEqualTo(expected);
    }

    private static Stream<Arguments> getParametersForFindAnnotatedHttpMethodAnnotation() {
        return Stream.of(
            Arguments.of(ClassOne.class, "test1", GetMapping.class),
            Arguments.of(ClassOne.class, "test2", PostMapping.class),
            Arguments.of(ClassOne.class, "test3", PutMapping.class),
            Arguments.of(ClassOne.class, "test4", DeleteMapping.class)
        );
    }

    @DisplayName("메서드에 매핑된 HttpMethodAnnotaion을 추출한다. - 실패")
    @Test
    void findAnnotatedHttpMethodAnnotation_ExtractMappedHttpMethodAnnotation_Fail() throws NoSuchMethodException {
        Method method = ClassFour.class.getMethod("test1");

        assertThatThrownBy(() -> HttpMethodMapper.findAnnotatedHttpMethodAnnotation(method))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
