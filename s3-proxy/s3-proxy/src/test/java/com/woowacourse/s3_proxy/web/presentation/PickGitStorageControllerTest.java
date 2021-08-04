package com.woowacourse.s3_proxy.web.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.s3_proxy.common.FileFactory;
import com.woowacourse.s3_proxy.exception.upload.UploadFailureException;
import com.woowacourse.s3_proxy.web.application.PickGitStorageService;
import com.woowacourse.s3_proxy.web.application.dto.FilesDto;
import com.woowacourse.s3_proxy.web.presentation.dto.Files;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(PickGitStorageController.class)
class PickGitStorageControllerTest {

    @MockBean
    private PickGitStorageService pickGitStorageService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Multipart 데이터의 수신을 확인한다. - 성공")
    @Test
    void store_CheckReceivingMultipartFile_True() throws Exception {
        final List<String> result = List.of("url1", "url2");

        //given
        MockMultipartFile image1 = FileFactory.getTestRightImage1();
        MockMultipartFile image2 = FileFactory.getTestRightImage2();

        given(pickGitStorageService.store(any()))
                .willReturn(new FilesDto.Response(result));

        //when
        MvcResult mvcResult = mockMvc.perform(multipart("/api/storage")
                .file(image1)
                .file(image2)
        ).andReturn();

        //then
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo(objectMapper.writeValueAsString(new Files.Response(result)));
    }

    @DisplayName("정상 이미지와 비정상 이미지를 전송한다 - 실패.")
    @Test
    void store_SendRightImageAndFailData_False() throws Exception {
        //when
        ResultActions resultActions = mockMvc.perform(
            multipart("/api/storage")
            .file(FileFactory.getTestRightImage1())
            .file(FileFactory.getTestFailData())
        );

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("errorCode")
            .value(new UploadFailureException().getErrorCode()));
    }

    @DisplayName("비정상 이미지를 전송한다 - 실패.")
    @Test
    void store_SendRFailDatas_False() throws Exception {
        //when
        ResultActions resultActions = mockMvc.perform(
            multipart("/api/storage")
                .file(FileFactory.getTestFailImage1())
                .file(FileFactory.getTestFailData())
        );

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("errorCode")
            .value(new UploadFailureException().getErrorCode()));
    }
}
