package com.woowacourse.s3_proxy.web.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.woowacourse.s3_proxy.common.FileFactory;
import com.woowacourse.s3_proxy.exception.upload.UploadFailureException;
import com.woowacourse.s3_proxy.web.domain.PickGitStorage.StoreResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class S3StorageTest {

    @InjectMocks
    private S3Storage s3Storage;

    @Mock
    private FileNameGenerator fileNameGenerator;

    @Mock
    private AmazonS3 amazonS3;

    private String fileUrlFormat = "https://djgd6o993rakk.cloudfront.net/images/%s";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Storage, "fileUrlFormat", fileUrlFormat);
    }

    @DisplayName("이미지 파일을 업로드한 뒤, 결과를 반환한다.")
    @Test
    void store_Results_Success() {
        List<MultipartFile> multipartFiles = List.of(
            FileFactory.getTestRightImage1(),
            FileFactory.getTestRightImage2()
        );
        String userName = "kevin";

        given(fileNameGenerator.generate(any(MultipartFile.class), eq(userName)))
            .willCallRealMethod();
        given(amazonS3.putObject(any(), any(), any(), any()))
            .willReturn(new PutObjectResult());

        List<StoreResult> actual = s3Storage.store(multipartFiles, userName);
        List<StoreResult> expected = generateSuccessResults(multipartFiles, userName);

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private List<StoreResult> generateSuccessResults(
        List<MultipartFile> multipartFiles, String userName
    ) {
        String firstFileName = fileNameGenerator.generate(multipartFiles.get(0), userName);
        String secondFileName = fileNameGenerator.generate(multipartFiles.get(1), userName);

        return List.of(
            new StoreResult(firstFileName, String.format(fileUrlFormat, firstFileName)),
            new StoreResult(secondFileName, String.format(fileUrlFormat, secondFileName))
        );
    }

    @DisplayName("복수의 이미지 파일을 업로드할 때 한 장이라도 실패하면 예외가 발생한다.")
    @Test
    void store_Results_Failure() {
        List<MultipartFile> multipartFiles = List.of(
            FileFactory.getTestRightImage1(),
            FileFactory.getTestRightImage2()
        );
        String userName = "kevin";

        given(fileNameGenerator.generate(any(MultipartFile.class), eq(userName)))
            .willCallRealMethod();
        given(amazonS3.putObject(any(), any(), any(), any()))
            .willThrow(RuntimeException.class);

        assertThatCode(() -> s3Storage.store(multipartFiles, userName))
            .isInstanceOf(UploadFailureException.class)
            .hasFieldOrPropertyWithValue("errorCode", "I0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasFieldOrPropertyWithValue("message", "업로드 실패");
    }
}
