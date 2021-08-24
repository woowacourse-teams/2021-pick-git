package com.woowacourse.pickgit.config.auth_interceptor_register.scanner;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.package_scanner.SourceVisitor;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SourceVisitorTest {

    private SourceVisitor sourceVisitor;

    @BeforeEach
    void setUp() throws IOException {
        String startsWith = getClass().getCanonicalName().split("\\.")[0];
        sourceVisitor = new SourceVisitor(startsWith);
    }

    @DisplayName("CONTINUE를 반환한다.")
    @Test
    void preVisitDirectory() throws IOException {
        Path file = createTempFile(".java");
        FileVisitResult actual = sourceVisitor.preVisitDirectory(file, null);

        assertThat(actual).isEqualTo(FileVisitResult.CONTINUE);
    }

    @DisplayName("test 폴더인 경우 무시하고 탐색한다.")
    @Test
    void preVisitDirectory_testDirectory() throws IOException, URISyntaxException {
        String uri = getClass().getResource(".").toURI().toASCIIString();
        Path file = Path.of(new URI(uri.substring(0, uri.indexOf("test") + 4)));
        FileVisitResult actual = sourceVisitor.preVisitDirectory(file, null);

        assertThat(actual).isEqualTo(FileVisitResult.SKIP_SUBTREE);
    }

    @DisplayName(".java 파일에서 package name을 추출한다.")
    @Test
    void visitFile_javaFile() throws IOException, URISyntaxException {
        String fileName = getClass().getSimpleName();
        String src =
            Objects.requireNonNull(getClass().getResource(".")).toURI() + fileName + ".class";
        Path file = Path.of(new URI(src));

        FileVisitResult actual = sourceVisitor.visitFile(file, null);
        List<String> classPaths = sourceVisitor.getClassPaths();

        assertThat(actual).isEqualTo(FileVisitResult.CONTINUE);
        assertThat(classPaths).containsExactly(
            String.format("%s.%s", getClass().getPackageName(), fileName)
        );
    }

    @DisplayName(".java 파일이 아니라면 무시한다")
    @Test
    void visitFile_otherFiles() throws IOException {
        Path file = createTempFile(".tmp");

        FileVisitResult actual = sourceVisitor.visitFile(file, null);
        List<String> classPaths = sourceVisitor.getClassPaths();

        assertThat(actual).isEqualTo(FileVisitResult.CONTINUE);
        assertThat(classPaths).hasSize(0);
    }

    @DisplayName("CONTINUE를 반환한다.")
    @Test
    void visitFileFailed() throws IOException {
        Path file = createTempFile(".java");
        FileVisitResult actual = sourceVisitor.visitFileFailed(file, null);

        assertThat(actual).isEqualTo(FileVisitResult.CONTINUE);
    }

    @DisplayName("CONTINUE를 반환한다.")
    @Test
    void postVisitDirectory() throws IOException {
        Path file = createTempFile(".java");
        FileVisitResult actual = sourceVisitor.postVisitDirectory(file, null);

        assertThat(actual).isEqualTo(FileVisitResult.CONTINUE);
    }

    private Path createTempFile(String suffix) throws IOException {
        return Files.write(
            Files.createTempFile("temp", suffix),
            String.join(System.lineSeparator(), List.of(
                "package com.woowacourse.pickgit.config.auth_interceptor_register.scanner;",
                "test",
                "test2",
                "test3"
            )).getBytes()
        );
    }
}
