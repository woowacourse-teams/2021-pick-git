package com.woowacourse.pickgit.config.auth_interceptor_register.scanner;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.package_scanner.PackageScanner;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PackageScannerTest {

    @DisplayName("루트 폴더 내부를 순회하며 class파일의 이름을 추출한다.")
    @Test
    void getAllClassNames_extractControllerJavaFilesName_Success() throws URISyntaxException {
        PackageScanner packageScanner =
            new PackageScanner("com.woowacourse.pickgit");
        List<String> allClassNames = packageScanner.getAllClassNames();

        assertThat(allClassNames).isNotEmpty();
        assertThat(allClassNames).contains(
                "com.woowacourse.pickgit.PickGitApplication"
            );
    }
}
