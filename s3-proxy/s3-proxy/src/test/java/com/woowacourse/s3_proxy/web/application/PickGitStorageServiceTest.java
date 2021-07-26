package com.woowacourse.s3_proxy.web.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.woowacourse.s3_proxy.common.FileFactory;
import com.woowacourse.s3_proxy.exception.upload.UploadFailureException;
import com.woowacourse.s3_proxy.web.application.dto.FilesDto.Request;
import com.woowacourse.s3_proxy.web.application.dto.FilesDto.Response;
import com.woowacourse.s3_proxy.web.domain.PickGitStorage;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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

    @DisplayName("파일 저장이 1개라도 실패하면 예외가 발생한다.")
    @Test
    void store_storeFiles_Failure() {
        //given
        given(pickGitStorage.store(anyList(), anyString()))
            .willThrow(new UploadFailureException());

        //when
        assertThatCode(() -> {
            pickGitStorageService.store(
                new Request("neozal", List.of(
                    FileFactory.getTestRightImage1(),
                    FileFactory.getTestRightImage2()
                ))
            );
        }).isInstanceOf(UploadFailureException.class)
            .hasFieldOrPropertyWithValue("errorCode", "I0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
            .hasFieldOrPropertyWithValue("message", "업로드 실패");
    }
}
