package com.woowacourse.pickgit.config.auth_interceptor_register.scanner;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.package_scanner.PackageScanner;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.package_scanner.SourceVisitor;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.test_classes.ClassOne;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.test_classes.ClassThree;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.test_classes.ClassTwo;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.test_classes.inner.ClassFour;
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

    @DisplayName("루트 폴더 내부를 순회하며 class파일의 이름을 추출한다.")
    @Test
    void getAllClassNames_extractControllerJavaFilesName_Success() throws URISyntaxException {
        PackageScanner packageScanner =
            new PackageScanner("com.woowacourse.pickgit", new TestSourceVisitor("com"));
        List<String> allClassNames = packageScanner.getAllClassNames();

        assertThat(allClassNames).isNotEmpty();
        assertThat(allClassNames).contains(
                "com.woowacourse.pickgit.PickGitApplication"
            );
    }

    private static class TestSourceVisitor extends SourceVisitor {

        public TestSourceVisitor(String startsWith) {
            super(startsWith);
        }

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
