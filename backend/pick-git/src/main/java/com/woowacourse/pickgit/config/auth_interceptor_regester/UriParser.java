package com.woowacourse.pickgit.config.auth_interceptor_regester;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.config.auth_interceptor_regester.register_type.RegisterType;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.ControllerScanner;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.ForGuestScanner;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.ForLoginUserScanner;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.MethodMapper;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.data_structure.PreparedControllerMethod;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.data_structure.PrepreparedControllerMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public class UriParser {

    private final ControllerScanner controllerScanner;
    private final ForGuestScanner forGuestScanner;
    private final ForLoginUserScanner forLoginUserScanner;

    public UriParser(ControllerScanner controllerScanner,
        ForGuestScanner forGuestScanner,
        ForLoginUserScanner forLoginUserScanner
    ) {
        this.controllerScanner = controllerScanner;
        this.forGuestScanner = forGuestScanner;
        this.forLoginUserScanner = forLoginUserScanner;
    }

    public List<PreparedControllerMethod> getPreparedControllerMethod() {
        return controllerScanner.getControllers().stream()
            .flatMap(controller -> generatePreparedControllerMethod(controller).stream())
            .collect(toList());
    }

    private List<PreparedControllerMethod> generatePreparedControllerMethod(Class<?> controller) {
        var prefixUrlsFromController = getPrefixUrlsFromController(controller);
        var urlsFromMethodsWithHttpMethod =
            getUrlsFromMethodsWithMethod(controller);

        List<PreparedControllerMethod> preparedControllerMethods = new ArrayList<>();

        prefixUrlsFromController.forEach(
            prefix ->preparedControllerMethods.addAll(
                createPreparedControllerMethods(urlsFromMethodsWithHttpMethod, prefix)
            )
        );

        return preparedControllerMethods;
    }

    private List<PrepreparedControllerMethod> getUrlsFromMethodsWithMethod(Class<?> controller) {
        List<PrepreparedControllerMethod> result = new ArrayList<>();

        result.addAll(createUrlsAndMethods(
            forLoginUserScanner.parseMethods(controller),
            RegisterType.AUTHENTICATE));

        result.addAll(createUrlsAndMethods(
            forGuestScanner.parseMethods(controller),
            RegisterType.IGNORE_AUTHENTICATE));

        return result;
    }

    private List<PreparedControllerMethod> createPreparedControllerMethods(
        List<PrepreparedControllerMethod> urlsFromMethodsWithHttpMethod,
        String prefix
    ) {
        List<PreparedControllerMethod> preparedControllerMethods = new ArrayList<>();

        for (PrepreparedControllerMethod prepreparedControllerMethod : urlsFromMethodsWithHttpMethod) {
            var httpMethod = prepreparedControllerMethod.getHttpMethod();
            var registerType = prepreparedControllerMethod.getRegisterType();
            var urls = prepreparedControllerMethod.getUrls();

            List<PreparedControllerMethod> values =
                createPreparedControllerMethod(prefix, httpMethod, registerType, urls);

            preparedControllerMethods.addAll(values);
        }

        return preparedControllerMethods;
    }

    private List<PreparedControllerMethod> createPreparedControllerMethod(String prefix,
        HttpMethod httpMethod, RegisterType registerType, List<String> urls) {
        List<PreparedControllerMethod> values = urls.stream()
            .map(url -> createUri(prefix, url))
            .map(completeUrl ->
                new PreparedControllerMethod(completeUrl, httpMethod, registerType))
            .collect(toList());
        return values;
    }

    private List<String> getPrefixUrlsFromController(Class<?> typeToken) {
        RequestMapping requestMapping = typeToken.getDeclaredAnnotation(RequestMapping.class);

        if (requestMapping == null) {
            return List.of("");
        }

        return List.of(requestMapping.value());
    }


    private List<PrepreparedControllerMethod> createUrlsAndMethods(
        List<Method> methods,
        RegisterType registerType
    ) {
        return methods.stream()
            .map(toPrepreparedControllerMethod(registerType))
            .collect(toList());
    }

    private Function<Method, PrepreparedControllerMethod> toPrepreparedControllerMethod(
        RegisterType registerType
    ) {
        return method -> new PrepreparedControllerMethod(
            parseUrlsFromMethod(method),
            parseHttpMethod(method),
            registerType
        );
    }

    private List<String> parseUrlsFromMethod(Method method) {
        Class<? extends Annotation> annotatedHttpMethodAnnotation = MethodMapper
            .findAnnotatedHttpMethodAnnotation(method);

        String name = annotatedHttpMethodAnnotation.getName();

        if (name.contains("Post")) {
            return List.of(method.getAnnotation(PostMapping.class).value());
        }

        if (name.contains("Get")) {
            return List.of(method.getAnnotation(GetMapping.class).value());
        }

        if (name.contains("Delete")) {
            return List.of(method.getAnnotation(DeleteMapping.class).value());
        }

        if (name.contains("Put")) {
            return List.of(method.getAnnotation(PutMapping.class).value());
        }

        throw new IllegalArgumentException("일치하는 Method mapping을 찾을 수 없습니다.");
    }

    private HttpMethod parseHttpMethod(Method method) {
        return MethodMapper.findHttpMethodByControllerMethod(method);
    }

    private String createUri(String... urlPieces) {
        String createdUri = "/" + Arrays.stream(urlPieces)
            .flatMap(urlPiece -> Arrays.stream(urlPiece.split("/")))
            .filter(urlPiece -> !urlPiece.isBlank())
            .collect(joining("/"));

        return createdUri.replaceAll("\\{.*?.}", "*");
    }
}
