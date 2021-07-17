package com.woowacourse.s3proxy.web.infrastructure;

import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.woowacourse.s3proxy.common.FileFactory;
import com.woowacourse.s3proxy.config.StorageConfiguration;
import com.woowacourse.s3proxy.config.StorageTestConfiguration;
import com.woowacourse.s3proxy.web.domain.PickGitStorage;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.stream.Collectors.teeing;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@Import(StorageTestConfiguration.class)
@LocalstackDockerProperties(services = {"s3"}, platform = "linux/x86_64")
@ExtendWith(LocalstackDockerExtension.class)
@ActiveProfiles("test")
@SpringBootTest
class S3StorageIntegrationTest {

    @Value("${aws.cloud_front.file_url_format}")
    private String fileUrlFormat;

    @Autowired
    private S3Storage s3Storage;

    @DisplayName("S3에 파일을 업로드 하고 결과를 확인한다. - 성공")
    @Test
    void store_UplaodFilesToS3AndCheckReturn_Ture() {
        MockMultipartFile image1 = FileFactory.getTestRightImage1();
        MockMultipartFile image2 = FileFactory.getTestRightImage2();
        String userName = "testUser";

        List<PickGitStorage.StoreResult> storeResults =
                s3Storage.store(List.of(image1, image2), userName);

        assertThat(storeResults)
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        createStoreResult(image1.getOriginalFilename()),
                        createStoreResult(image2.getOriginalFilename())
                        )
                );
    }

    @DisplayName("S3에 파일을 업로드 하고 실패한 파일과 성공한 파일의 결과를 확인한다. - 성공")
    @Test
    void store_UploadFilesToS3AndCheckReturnThatHasFailAndSuccessResult_True() {
        MockMultipartFile image1 = FileFactory.getTestRightImage1();
        MockMultipartFile image2 = FileFactory.getTestRightImage2();
        String userName = "testUser";

        List<Boolean> succeeds = s3Storage.store(List.of(image1, image2), userName).stream()
                .map(PickGitStorage.StoreResult::isSucceed)
                .filter(b -> b)
                .collect(toList());

        assertThat(succeeds).hasSize(2);
    }


    private PickGitStorage.StoreResult createStoreResult(String fileName) {
        return new PickGitStorage.StoreResult(
                fileName,
                String.format(fileUrlFormat, fileName)
        );
    }
}


