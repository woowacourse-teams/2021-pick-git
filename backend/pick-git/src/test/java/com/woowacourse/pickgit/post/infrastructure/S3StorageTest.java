package com.woowacourse.pickgit.post.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.woowacourse.pickgit.common.FileFactory;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class S3StorageTest {

    @InjectMocks
    private S3Storage s3Storage;

    @Mock
    private RestClient restClient;

    @DisplayName("이미지를 보내면 이미지 주소를 반환한다.")
    @Test
    void store_IfImagesGivenReturnUrls_True() {
        //given
        List<String> expected = List.of("testUrl1", "testUrl2");
        given(restClient.postForEntity(any(), any(), any(), (Object) any()))
            .willReturn(ResponseEntity.ok(
                new S3Storage.StorageDto(expected))
            );

        List<String> actual = s3Storage.store(List.of(
            FileFactory.getTestImage1File(),
            FileFactory.getTestImage2File()
        ), "testUser");

        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(actual);
    }
}
