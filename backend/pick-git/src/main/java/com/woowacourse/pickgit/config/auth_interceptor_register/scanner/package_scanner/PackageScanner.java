package com.woowacourse.pickgit.config.auth_interceptor_register.scanner.package_scanner;

import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class PackageScanner {

    private final String basePackage;

    public PackageScanner(String basePackage) {
        this.basePackage = basePackage;
    }

    public List<String> getAllClassNames() {
        Reflections reflections = new Reflections(
            basePackage,
            new SubTypesScanner(false)
        );

        return new HashSet<>(reflections.getSubTypesOf(Object.class)).stream()
            .map(Class::getName)
            .collect(toList());
    }
}
