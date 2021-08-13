package com.woowacourse.pickgit.config.auth_interceptor_register.scanner.package_scanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PackageScanner {

    private final Path rootPath;
    private final SourceVisitor sourceVisitor;

    public PackageScanner(SourceVisitor sourceVisitor) {
        this(
            Paths.get(".").normalize().toAbsolutePath(),
            sourceVisitor
        );
    }

    public PackageScanner(Path rootPath, SourceVisitor sourceVisitor) {
        this.rootPath = rootPath;
        this.sourceVisitor = sourceVisitor;
    }

    public List<String> getAllClassNames() {
        try {
            Files.walkFileTree(rootPath, sourceVisitor);
            return sourceVisitor.getClassPaths();
        } catch (IOException e) {
            throw new IllegalArgumentException("Interceptor register: 파일 순회 오류", e);
        }
    }

}
