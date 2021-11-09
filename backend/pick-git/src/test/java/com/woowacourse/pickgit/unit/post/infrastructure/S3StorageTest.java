package com.woowacourse.pickgit.unit.post.infrastructure;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.post.infrastructure.S3Storage;
import com.woowacourse.pickgit.post.infrastructure.S3Storage.StorageDto;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

class S3StorageTest {

    private S3Storage s3Storage;

    @BeforeEach
    void setUp() {
        s3Storage = new S3Storage(
            new StubRestClient() {
                @Override
                @SuppressWarnings({"rawtypes", "unchecked"})
                public <T> ResponseEntity<T> postForEntity(
                    String url,
                    @Nullable Object request,
                    Class<T> responseType,
                    Object... uriVariables
                ) {
                    MultiValueMap datas = (MultiValueMap) request;

                    ArrayList<FileSystemResource> fileSystemResources =
                        (ArrayList<FileSystemResource>) datas.get("files");

                    if(Objects.isNull(fileSystemResources)) {
                        fileSystemResources = new ArrayList<>();
                    }

                    List<String> fileNames = fileSystemResources.stream()
                        .map(FileSystemResource::getFile)
                        .map(File::getName)
                        .collect(toList());

                    return ResponseEntity.ok(
                        responseType.cast(
                            new StorageDto(fileNames)
                        )
                    );
                }
            },
            "testS3ProxyUrl"
        );
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
}
