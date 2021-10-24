package com.woowacourse.s3_proxy.web.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.s3_proxy.common.FileFactory;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.multipart.MultipartFile;

class FileNameGeneratorTest {

    private final FileNameGenerator fileNameGenerator = new FileNameGenerator();

    @DisplayName("반환된 파일 이름은 32자이다.")
    @Test
    void generate() {
        MultipartFile multipartFile = FileFactory.getTestRightImage1();
        String generatedFileName = generateFileName(multipartFile);

        int extensionLength = generatedFileName.length() - generatedFileName.indexOf(".");
        int fileNameLength = generatedFileName.length() - extensionLength;

        assertThat(fileNameLength).isEqualTo(32);
    }

    private String generateFileName(MultipartFile multipartFile) {
        final String userName = "testUser";

        return fileNameGenerator.generate(multipartFile, userName);
    }

    @DisplayName("제공된 파일의 타입과 일치하는 확장자를 반환한다.")
    @ParameterizedTest
    @MethodSource("createFilesForGenerateTest")
    void extension(MultipartFile multipartFile, String extension) {
        String generatedFileName = generateFileName(multipartFile);

        assertThat(generatedFileName).endsWith(extension);
    }

    private static Stream<Arguments> createFilesForGenerateTest() {
        return Stream.of(
            Arguments.of(FileFactory.getTestRightImage1(), ".png"),
            Arguments.of(FileFactory.getTestRightImage2(), ".png"),
            Arguments.of(FileFactory.getTestFailData(), ".sh"),
            Arguments.of(FileFactory.getTestFailImage1(), ".txt")
        );
    }
}
