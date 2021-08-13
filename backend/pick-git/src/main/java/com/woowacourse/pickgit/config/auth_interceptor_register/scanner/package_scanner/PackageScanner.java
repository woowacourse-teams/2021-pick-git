package com.woowacourse.pickgit.config.auth_interceptor_register.scanner.package_scanner;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class PackageScanner {

    private final String basePackage;
    private final SourceVisitor sourceVisitor;

    public PackageScanner(String basePackage, SourceVisitor sourceVisitor) {
        this.basePackage = basePackage;
        this.sourceVisitor = sourceVisitor;
    }

    public List<String> getAllClassNames() {
        try {
            Files.walkFileTree(getBaseUri(), sourceVisitor);
            return sourceVisitor.getClassPaths();
        } catch (IOException e) {
            throw new IllegalArgumentException("Interceptor register: 파일 순회 오류", e);
        }
    }

    private Path getBaseUri() {
        try {
            URI baseUri = Objects.requireNonNull(ClassLoader.getSystemResource(".")).toURI();
            String stringBaseUri = baseUri.normalize().getPath();

            stringBaseUri = stringBaseUri.replaceAll("\\/test\\/", "/production/");
            String uri = basePackage.replaceAll("[.]", "/");

            return new File(stringBaseUri + uri).toPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
