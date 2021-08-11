package com.woowacourse.pickgit.config.auth_interceptor_regester.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

public enum MethodMapper {
    GET(GetMapping.class, HttpMethod.GET),
    POST(PostMapping.class, HttpMethod.POST),
    DELETE(DeleteMapping.class, HttpMethod.DELETE),
    PUT(PutMapping.class, HttpMethod.PUT);

    private final Class<? extends Annotation> mappingAnnotation;
    private final HttpMethod httpMethod;

    MethodMapper(Class<? extends Annotation> mappingAnnotation, HttpMethod httpMethod) {
        this.mappingAnnotation = mappingAnnotation;
        this.httpMethod = httpMethod;
    }

    public static HttpMethod findHttpMethodByControllerMethod(Method method) {
        return Arrays.stream(values())
            .filter(
                httpMethod -> !Objects.isNull(method.getAnnotation(httpMethod.mappingAnnotation)))
            .map(httpMethod -> httpMethod.httpMethod)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("매칭되는 http method가 없습니다."));
    }

    public static Class<? extends Annotation> findAnnotatedHttpMethodAnnotation(Method method) {
        return Arrays.stream(values())
            .filter(
                httpMethod -> !Objects.isNull(method.getAnnotation(httpMethod.mappingAnnotation)))
            .map(httpMethod -> httpMethod.mappingAnnotation)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("매칭되는 http methodAnnotation이 없습니다."));
    }
}
