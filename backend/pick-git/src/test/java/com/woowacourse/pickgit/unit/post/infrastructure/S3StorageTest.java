package com.woowacourse.pickgit.unit.post.infrastructure;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.post.infrastructure.S3Requester;
import com.woowacourse.pickgit.post.infrastructure.S3Storage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

class S3StorageTest {

    private S3Storage s3Storage;

    @BeforeEach
    void setUp() {
        s3Storage = new S3Storage(new MockS3Requester(null), "testS3ProxyUrl");
    }

    @DisplayName("이미지 저장을 요청하면 url을 반환한다.")
    @Test
    void store_RequestToSaveImages_ReturnImageUrls() {
        File testImage1File = FileFactory.getTestImage1File();
        File testImage2File = FileFactory.getTestImage2File();

        List<File> imageFiles = List.of(
            testImage1File, testImage2File
        );

        List<String> fileUrls = s3Storage.store(imageFiles, "testUser");

        assertThat(fileUrls).containsExactly(
            testImage1File.getName(),
            testImage2File.getName()
        );
    }

    @DisplayName("이미지 없이 저장을 요청하면 빈 배열을 요청한다.")
    @Test
    void store() {
        List<File> imageFiles = List.of();

        List<String> fileUrls = s3Storage.store(imageFiles, "testUser");

        assertThat(fileUrls).isEmpty();
    }

    private static class MockS3Requester extends S3Requester {

        public MockS3Requester(WebClient webClient) {
            super(webClient);
        }

        @Override
        public List<String> storeImages(String url, MultiValueMap<String, Object> body) {
            List<Object> fileSystemResources = body.get("files");

            if (Objects.isNull(fileSystemResources)) {
                fileSystemResources = new ArrayList<>();
            }

            return fileSystemResources.stream()
                .map(t -> ((FileSystemResource) t).getFile())
                .map(File::getName)
                .collect(toList());
        }
    }
}
