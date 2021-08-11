package com.woowacourse.pickgit.config.auth_interceptor_regester.scanner;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.package_scanner.PackageScanner;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.package_scanner.SourceVisitor;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes.ClassOne;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes.ClassThree;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes.ClassTwo;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes.inner.ClassFour;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PackageScannerTest {

    @DisplayName("루트 폴더 내부를 순회하며 Controller java파일의 이름을 추출한다.")
    @Test
    void name() throws URISyntaxException {
        String resource = ClassOne.class.getResource(".")
            .toString()
            .replace("/build/classes/java/test", "/src/test/java");

        Path rootPath = Path.of(new URI(resource));
        PackageScanner packageScanner = new PackageScanner(rootPath, new TestSourceVisitor());
        List<String> allClassNames = packageScanner.getAllClassNames();

        assertThat(allClassNames)
            .contains(
                ClassOne.class.getCanonicalName(),
                ClassTwo.class.getCanonicalName(),
                ClassThree.class.getCanonicalName(),
                ClassFour.class.getCanonicalName()
            );
    }

    private static class TestSourceVisitor extends SourceVisitor {

        public List<String> getClassPaths() {
            return super.getClassPaths();
        }

        @Override
        public FileVisitResult preVisitDirectory(
            Path dir, BasicFileAttributes attrs
        ) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }
}