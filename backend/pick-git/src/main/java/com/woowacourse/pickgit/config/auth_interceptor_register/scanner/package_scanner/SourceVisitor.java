package com.woowacourse.pickgit.config.auth_interceptor_register.scanner.package_scanner;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class SourceVisitor implements FileVisitor<Path> {

    private final String basePackage;
    private final List<String> classPaths;

    public SourceVisitor(String basePackage) {
        this(basePackage, new ArrayList<>());
    }

    public SourceVisitor(String basePackage, List<String> classPaths) {
        this.basePackage = basePackage.replaceAll("[.]", "/");
        this.classPaths = classPaths;
    }

    public List<String> getClassPaths() {
        return classPaths;
    }

    @Override
    public FileVisitResult preVisitDirectory(
        Path dir, BasicFileAttributes attrs
    ) throws IOException {
        if (dir.getFileName().toString().contains("test")) {
            return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (!file.toString().endsWith(".class")) {
            return FileVisitResult.CONTINUE;
        }

        String classSource = file.toUri().toString();
        int index = classSource.indexOf(basePackage);
        if (index == -1) {
            return FileVisitResult.CONTINUE;
        }

        String classPath = assemblePackage(classSource, index);
        this.classPaths.add(classPath);

        return FileVisitResult.CONTINUE;
    }

    private String assemblePackage(String base, int index) {
        base = base.substring(index);
        base = base.replace("/", ".");

        return base.replace(".class", "");
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
