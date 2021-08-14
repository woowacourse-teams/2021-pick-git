package com.woowacourse.pickgit.config.auth_interceptor_register.scanner.package_scanner;

import com.woowacourse.pickgit.config.auth_interceptor_register.util.Unpack;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PackageScanner {

    private final String basePackage;
    private final SourceVisitor sourceVisitor;

    public PackageScanner(
        String basePackage,
        SourceVisitor sourceVisitor
    ) {
        this.basePackage = basePackage;
        this.sourceVisitor = sourceVisitor;
    }

    public List<String> getAllClassNames() {
        try {
            for (Path baseURL : getBaseURLs()) {
                Files.walkFileTree(baseURL, sourceVisitor);
            }

            return sourceVisitor.getClassPaths();
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException("Interceptor register: 파일 순회 오류", e);
        }
    }

    private List<Path> getBaseURLs() throws URISyntaxException {
        if (getCurrentPath().endsWith(".jar")) {
            return getBaseURLsOnJar();
        }

        return getBaseURLsOnDirectory();
    }

    private List<Path> getBaseURLsOnJar() throws URISyntaxException {
        String currentPath = getCurrentPath();
        File jar = Unpack.jar(new File(currentPath));
        return List.of(jar.toPath());
    }

    private List<Path> getBaseURLsOnDirectory() {
        try {
            Enumeration<URL> resources = ClassLoader
                .getSystemClassLoader()
                .getResources(basePackage.replaceAll("[.]", "/"));

            ArrayList<Path> urls = new ArrayList<>();

            while (resources.hasMoreElements()) {
                String url = resources.nextElement().getPath();
                urls.add(Path.of(url));
            }

            return urls;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCurrentPath() throws URISyntaxException {
        String path = getClass()
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .toURI()
            .toString();

        path = path.replace("file:", "");
        if (path.startsWith("jar")) {
            path = path.replace("jar:", "");
            path = path.substring(0, path.indexOf("!/"));
        }

        return path;
    }

}
