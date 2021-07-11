package com.woowacourse.s3proxy.web.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import com.woowacourse.s3proxy.web.application.dto.FilesDto.Request;
import com.woowacourse.s3proxy.web.application.dto.FilesDto.Response;
import com.woowacourse.s3proxy.web.domain.PickGitStorage;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class PickGitStorageServiceTest {

    @Mock
    private PickGitStorage pickGitStorage;

    @InjectMocks
    private PickGitStorageService pickGitStorageService;


    @Test
    void store() {
        //given
        final String testFileName = "testFileName";
        final String testUrl = "testUrl";
        given(pickGitStorage.store(anyList()))
            .willReturn(Collections.singletonList(
                new PickGitStorage.StoreResult(testFileName, testUrl)
            ));

        //when
        Response store = pickGitStorageService.store(new Request("neozal", List.of(
            createTestFile("testFileName")
        )));

        //then
        assertThat(store.getUrls()).contains(testUrl);
    }

    private MockMultipartFile createTestFile(String fileName) {
        return new MockMultipartFile(
            "files",
            fileName,
            ContentType.IMAGE_JPEG.toString(),
            "TEST".getBytes(StandardCharsets.UTF_8)
        );
    }
}
