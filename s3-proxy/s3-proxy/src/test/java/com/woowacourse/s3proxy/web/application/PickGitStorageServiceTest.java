package com.woowacourse.s3proxy.web.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.woowacourse.s3proxy.common.FileFactory;
import com.woowacourse.s3proxy.exception.UploadFailException;
import com.woowacourse.s3proxy.web.application.dto.FilesDto.Request;
import com.woowacourse.s3proxy.web.application.dto.FilesDto.Response;
import com.woowacourse.s3proxy.web.domain.PickGitStorage;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("파일들을 저장한다. - 성공")
    @Test
    void store_storeFiles_True() {
        //given
        MockMultipartFile testRightImage1 = FileFactory.getTestRightImage1();
        MockMultipartFile testRightImage2 = FileFactory.getTestRightImage2();
        final String testUrl = "testUrl";

        given(pickGitStorage.store(anyList(), anyString()))
            .willReturn(List.of(
                new PickGitStorage.StoreResult(testRightImage1.getOriginalFilename(), testUrl),
                new PickGitStorage.StoreResult(testRightImage2.getOriginalFilename(), testUrl)
            ));

        //when
        Response store = pickGitStorageService.store(
            new Request("neozal", List.of(
                FileFactory.getTestRightImage1(),
                FileFactory.getTestRightImage2()
            ))
        );

        //then
        assertThat(store.getUrls()).containsExactly(testUrl, testUrl);
    }

    @DisplayName("파일 저장에 실패한 것은 제외하여 응답을 반환한다.")
    @Test
    void store_storeFiles_Failure() {
        //given
        MockMultipartFile testFailImage1 = FileFactory.getTestFailImage1();
        MockMultipartFile testRightImage2 = FileFactory.getTestRightImage2();

        given(pickGitStorage.store(anyList(), anyString()))
            .willReturn(List.of(
                new PickGitStorage.StoreResult(
                    testFailImage1.getOriginalFilename(),
                    new UploadFailException(new RuntimeException())
                ),
                new PickGitStorage.StoreResult(
                    testRightImage2.getOriginalFilename(),
                    "testUrl")
                )
            );

        //when
        Response store = pickGitStorageService.store(
            new Request("neozal", List.of(
                FileFactory.getTestRightImage1(),
                FileFactory.getTestRightImage2()
            ))
        );

        //then
        assertThat(store.getUrls()).hasSize(1);
    }
}
