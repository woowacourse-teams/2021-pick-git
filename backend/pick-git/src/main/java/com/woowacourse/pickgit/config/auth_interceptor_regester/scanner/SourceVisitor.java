package com.woowacourse.pickgit.config.auth_interceptor_regester.scanner;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SourceVisitor implements FileVisitor<Path> {

    private final List<String> classPaths;

    public SourceVisitor() {
        this(new ArrayList<>());
    }

    public SourceVisitor(List<String> classPaths) {
        this.classPaths = classPaths;
    }

    public List<String> getClassPaths() {
        return classPaths;
    }

    @Override
    public FileVisitResult preVisitDirectory(
        Path dir, BasicFileAttributes attrs
    ) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if(!file.toString().endsWith(".java")) {
            return FileVisitResult.CONTINUE;
        }

        try(Stream<String> lines = Files.lines(file)) {
            String classPath = lines
                .filter(line -> line.startsWith("package"))
                .findAny()
                .map(line -> assemblePackage(line, file))
                .orElseThrow(() -> new IllegalArgumentException(".java 파일 파싱에 실패했습니다."));

            this.classPaths.add(classPath);
        }

        return FileVisitResult.CONTINUE;
    }

    private String assemblePackage(String base, Path file) {
        base = base.replace(";", "");
        base = base.replace("package ", "");
        String fileName = file.getFileName().toString().replace(".java", "");

        return String.format("%s.%s", base, fileName);
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
