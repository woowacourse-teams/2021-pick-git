package com.woowacourse.s3proxy.web.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.s3proxy.web.application.PickGitStorageService;
import com.woowacourse.s3proxy.web.application.dto.FilesDto;
import com.woowacourse.s3proxy.web.presentation.Dto.Files;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(PickGitStorageController.class)
class PickGitStorageControllerTest {

    @MockBean
    private PickGitStorageService pickGitStorageService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Multipart 데이터의 수신을 확인한다.")
    @Test
    void store() throws Exception {
        final List<String> result = List.of("url1", "url2");

        //given
        MockMultipartFile image1 = createTestFile("testImage1.jpg");
        MockMultipartFile image2 = createTestFile("testImage2.jpg");
        given(pickGitStorageService.store(any()))
                .willReturn(new FilesDto.Response(result));

        //when
        MvcResult mvcResult = mockMvc.perform(multipart("/storage")
                .file(image1)
                .file(image2)
        ).andReturn();

        //then
        assertThat(mvcResult.getResponse().getContentAsString())
                .isEqualTo(objectMapper.writeValueAsString(new Files.Response(result)));
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
