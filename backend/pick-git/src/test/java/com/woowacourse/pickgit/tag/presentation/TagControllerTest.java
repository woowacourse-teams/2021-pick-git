package com.woowacourse.pickgit.tag.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.tag.application.ExtractionRequestDto;
import com.woowacourse.pickgit.tag.application.TagService;
import com.woowacourse.pickgit.tag.application.TagsDto;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureRestDocs
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

    @BeforeEach
    void setUp() {
        LoginUser loginUser = new LoginUser(userName, accessToken);
        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(loginUser);
    }

    @DisplayName("특정 User의 Repository에 기술된 언어 태그들을 추출한다.")
    @Test
    void extractLanguageTags_ValidRepository_ExtractionSuccess() throws Exception {
        String url =
            "/api/github/repositories/{repositoryName}}/tags/languages";

        List<String> tags = Arrays.asList("Java", "Python", "HTML");
        TagsDto tagsDto = new TagsDto(tags);
        String expectedResponse = objectMapper.writeValueAsString(tagsDto.getTags());

        given(tagService.extractTags(any(ExtractionRequestDto.class)))
            .willReturn(tagsDto);

        ResultActions perform = mockMvc.perform(get(url, repositoryName)
            .header("Authorization", accessToken))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponse));

        verify(tagService, times(1))
            .extractTags(any(ExtractionRequestDto.class));

        perform.andDo(document("tag-extractTagFromRepositoryOfSpecificUser",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
            ),
            pathParameters(
                parameterWithName("repositoryName").description("레포지토리 이름")
            ),
            responseFields(
                fieldWithPath("[]").type(ARRAY).description("태그 목록")
            )
        ));
    }

    @DisplayName("유효하지 않은 AccessToken으로 태그 추출 요청시 401 예외 메시지가 반환된다.")
    @Test
    void extractLanguageTags_InvalidAccessToken_ExceptionThrown() throws Exception {
        String url =
            "/api/github/repositories/{repositoryName}}/tags/languages";

        given(oAuthService.validateToken(any(String.class)))
            .willReturn(false);

        ResultActions perform = mockMvc.perform(get(url, userName)
            .header("Authorization", "Bearer invalid"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("errorCode").value("A0001"));

        perform.andDo(document("tags-invalidToken",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
            ),
            pathParameters(
                parameterWithName("repositoryName").description("레포지토리 이름")
            ),
            responseFields(
                fieldWithPath("errorCode").type(STRING).description("에러 코드")
            )
        ));
    }

    @DisplayName("유효하지 않은 레포지토리 태그 추출 요청시 404 예외 메시지가 반환된다.")
    @Test
    void extractLanguageTags_InvalidRepository_ExceptionThrown() throws Exception {
        String url =
            "/api/github/repositories/{repositoryName}}/tags/languages";

        given(tagService.extractTags(any(ExtractionRequestDto.class)))
            .willThrow(new PlatformHttpErrorException());

        ResultActions perform = mockMvc.perform(get(url, userName, "invalidrepo")
            .header("Authorization", accessToken))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("errorCode").value("V0001"));

        verify(tagService, times(1))
            .extractTags(any(ExtractionRequestDto.class));

        perform.andDo(document("tags-invalidRepository",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
            ),
            pathParameters(
                parameterWithName("repositoryName").description("잘못된 레포지토리 이름")
            ),
            responseFields(
                fieldWithPath("errorCode").type(STRING).description("에러 코드")
            )
        ));
    }
}
