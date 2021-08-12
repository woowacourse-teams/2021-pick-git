package com.woowacourse.pickgit.config.auth_interceptor_register.scanner;

import static java.util.stream.Collectors.toList;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

public class ControllerScanner {

    private final List<String> classNames;

    public ControllerScanner(List<String> classNames) {
        this.classNames = classNames;
    }

    public List<Class<?>> getControllers() {
        return classNames.stream()
            .map(this::getClassByClassName)
            .filter(this::isController)
            .collect(toList());
    }

    private Class<?> getClassByClassName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("interceptor register: class 로드 실패", e);
        }
    }

    private boolean isController(Class<?> typeToken) {
        return typeToken.isAnnotationPresent(Controller.class) ||
            typeToken.isAnnotationPresent(RestController.class);
    }
}
