package com.woowacourse.pickgit.config.auth_interceptor_register.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

public enum HttpMethodMapper {
    GET(GetMapping.class, HttpMethod.GET,
        method -> List.of(method.getAnnotation(GetMapping.class).value())),
    POST(PostMapping.class, HttpMethod.POST,
        method -> List.of(method.getAnnotation(PostMapping.class).value())),
    DELETE(DeleteMapping.class, HttpMethod.DELETE,
        method -> List.of(method.getAnnotation(DeleteMapping.class).value())),
    PUT(PutMapping.class, HttpMethod.PUT,
        method -> List.of(method.getAnnotation(PutMapping.class).value()));

    private final Class<? extends Annotation> mappingAnnotation;
    private final HttpMethod httpMethod;
    private final Function<Method, List<String>> extractValues;

    HttpMethodMapper(
        Class<? extends Annotation> mappingAnnotation,
        HttpMethod httpMethod,
        Function<Method, List<String>> extractValues
    ) {
        this.mappingAnnotation = mappingAnnotation;
        this.httpMethod = httpMethod;
        this.extractValues = extractValues;
    }

    public static HttpMethod findHttpMethodByControllerMethod(Method method) {
        return Arrays.stream(values())
            .filter(
                httpMethod -> Objects.nonNull(method.getAnnotation(httpMethod.mappingAnnotation)))
            .map(httpMethod -> httpMethod.httpMethod)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("매칭되는 http method가 없습니다."));
    }

    public static Class<? extends Annotation> findAnnotatedHttpMethodAnnotation(Method method) {
        return Arrays.stream(values())
            .filter(
                httpMethod -> Objects.nonNull(method.getAnnotation(httpMethod.mappingAnnotation)))
            .map(httpMethod -> httpMethod.mappingAnnotation)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("매칭되는 http methodAnnotation이 없습니다."));
    }

    public static List<String> extractMappingValues(Method method) {
        return Arrays.stream(values())
            .filter(value -> method.isAnnotationPresent(value.mappingAnnotation))
            .map(value -> value.extractValues.apply(method))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("매칭되는 http methodAnnotation이 없습니다."));
    }
}
