package com.woowacourse.s3_proxy.common.file_validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.woowacourse.s3_proxy.common.FileFactory;
import com.woowacourse.s3_proxy.exception.upload.UploadFailureException;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.multipart.MultipartFile;

class ImageFileValidatorTest {

    private static ImageFileValidator imageFileValidator =
        new ImageFileValidator();

    @DisplayName("비정상 이미지 파일 - 실패")
    @ParameterizedTest
    @MethodSource("createFailDatas")
    void execute_FileMediaTypeIsNotImage_ExceptionThrown(MultipartFile multipartFile) {
        assertThatThrownBy(() -> imageFileValidator.execute(multipartFile))
            .isInstanceOf(UploadFailureException.class);
    }

    private static Stream<Arguments> createFailDatas() {
        return Stream.of(
            Arguments.of(FileFactory.getTestFailData()),
            Arguments.of(FileFactory.getTestFailImage1()),
            Arguments.of(FileFactory.getTestFailImage2())
        );
    }

    @DisplayName("정상 이미지 파일 - 성공")
    @ParameterizedTest
    @MethodSource("createRightDatas")
    void execute_FileMediaTypeIsImage_True(MultipartFile multipartFile) {
        assertDoesNotThrow(() -> imageFileValidator.execute(multipartFile));
    }

    private static Stream<Arguments> createRightDatas() {
        return Stream.of(
            Arguments.of(FileFactory.getTestRightImage1()),
            Arguments.of(FileFactory.getTestRightImage2())
        );
    }
}
