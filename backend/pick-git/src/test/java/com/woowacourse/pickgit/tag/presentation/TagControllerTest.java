package com.woowacourse.pickgit.tag.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.tag.application.ExtractionRequestDto;
import com.woowacourse.pickgit.tag.application.TagService;
import com.woowacourse.pickgit.tag.application.TagsDto;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TagController.class)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TagService tagService;

    @MockBean
    private OAuthService oAuthService;

    private String accessToken = "Bearer validtoken";
    private String userName = "abc";
    private String repositoryName = "repo";

    @DisplayName("특정 User의 Repository에 기술된 언어 태그들을 추출한다.")
    @Test
    void extractLanguageTags_ValidRepository_ExtractionSuccess() throws Exception {
        String url =
            "/api/github/" + userName + "/repositories/" + repositoryName + "/tags/languages";
        LoginUser loginUser = new LoginUser(userName, accessToken);
        List<String> tags = Arrays.asList("Java", "Python", "HTML");
        TagsDto tagsDto = new TagsDto(tags);
        String expectedResponse = objectMapper.writeValueAsString(tagsDto);

        given(oAuthService.validateToken(any(String.class)))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any(String.class)))
            .willReturn(loginUser);
        given(tagService.extractTags(any(ExtractionRequestDto.class)))
            .willReturn(tagsDto);

        mockMvc.perform(get(url)
            .header("Authorization", accessToken))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponse));

        verify(oAuthService, times(1))
            .validateToken(any(String.class));
        verify(oAuthService, times(1))
            .findRequestUserByToken(any(String.class));
        verify(tagService, times(1))
            .extractTags(any(ExtractionRequestDto.class));
    }

    @DisplayName("유효하지 않은 레포지토리 태그 추출 요청시 404 예외 메시지가 반환된다.")
    @Test
    void extractLanguageTags_InvalidRepository_ExceptionThrown() throws Exception {
        String url =
            "/api/github/" + userName + "/repositories/invalidrepo/tags/languages";
        LoginUser loginUser = new LoginUser(userName, accessToken);

        given(oAuthService.validateToken(any(String.class)))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any(String.class)))
            .willReturn(loginUser);
        given(tagService.extractTags(any(ExtractionRequestDto.class)))
            .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get(url)
            .header("Authorization", accessToken))
            .andExpect(status().isNotFound())
            .andExpect(content().string("외부 플랫폼 연동 요청 처리에 실패했습니다."));

        verify(oAuthService, times(1))
            .validateToken(any(String.class));
        verify(oAuthService, times(1))
            .findRequestUserByToken(any(String.class));
        verify(tagService, times(1))
            .extractTags(any(ExtractionRequestDto.class));
    }

    /*
    Interceptor 예외 핸들링이 구현되면 테스트도 변경되어야 합니다.
     */
//    @DisplayName("유효하지 않은 AccessToken으로 태그 추출 요청시 401 예외 메시지가 반환된다.")
//    @Test
//    void extractLanguageTags_InvalidAccessToken_ExceptionThrown() throws Exception {
//        String url =
//            "/api/github/" + userName + "/repositories/" + repositoryName + "/tags/languages";
//
//        given(tagService.extractTags(any(ExtractionRequestDto.class)))
//            .willThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
//
//        mockMvc.perform(get(url)
//            .header("Authorization", "Bearer invalid"))
//            .andExpect(status().isUnauthorized())
//            .andExpect(content().string("외부 플랫폼 연동 요청 처리에 실패했습니다."));
//    }
}
