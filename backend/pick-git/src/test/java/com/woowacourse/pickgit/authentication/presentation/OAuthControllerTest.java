package com.woowacourse.pickgit.authentication.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureRestDocs
@ExtendWith(SpringExtension.class)
@WebMvcTest(OAuthController.class)
@ActiveProfiles("test")
class OAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuthService oAuthService;

    @DisplayName("Github 로그인 요청을 하면 Github 인증 URL을 반환한다.")
    @Test
    void authorizationGithubUrl_InvalidAccount_GithubUrl() throws Exception {
        // given
        String githubAuthorizationGithubUrl = "http://github.authorization.url";
        when(oAuthService.getGithubAuthorizationUrl()).thenReturn(githubAuthorizationGithubUrl);

        // when, then
        ResultActions perform = mockMvc.perform(get("/api/authorization/github"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("url").value(githubAuthorizationGithubUrl));

        perform.andDo(document("authorization - githubLogin",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("url").type(STRING).description("Github login url")
            )
        ));
    }

    @DisplayName("Github 로그인 인증 후 토큰을 발행하여 반환한다.")
    @Test
    void afterAuthorizeGithubLogin_ValidAccount_JWTToken() throws Exception {
        // given
        String githubAuthorizationCode = "random";
        when(oAuthService.createToken(githubAuthorizationCode))
            .thenReturn(new TokenDto("jwt token", "binghe"));

        // when, then
        ResultActions perform = mockMvc
            .perform(get("/api/afterlogin?code=" + githubAuthorizationCode))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("token").value("jwt token"));

        perform.andDo(document("authorization - afterlogin",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("token").type(STRING).description("JWT 토큰"),
                fieldWithPath("username").type(STRING).description("유저 이름")
            )
        ));
    }
}
