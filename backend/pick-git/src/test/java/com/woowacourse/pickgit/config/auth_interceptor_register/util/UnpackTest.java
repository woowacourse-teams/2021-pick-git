package com.woowacourse.pickgit.config.auth_interceptor_register.util;

import static java.util.Collections.reverseOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnpackTest {

    @DisplayName("jar파일을 임시 폴더에 푼다.")
    @Test
    void jar() throws URISyntaxException, IOException {
        URL systemResource = ClassLoader.getSystemResource("testjar.jar");
        File unpackedDirectory = null;
        try {
            unpackedDirectory = new Unpack().jar(new File(systemResource.getFile()));
            assertThat(unpackedDirectory).exists();
        } finally {
            Path unpackedDirectoryPath = unpackedDirectory.toPath();

            Files.walk(unpackedDirectoryPath)
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }
}
