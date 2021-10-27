package com.woowacourse.pickgit.unit.authentication.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.unit.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

class OAuthControllerTest extends ControllerTest {

    @DisplayName("Github 로그인 요청을 하면 Github 인증 URL을 반환한다.")
    @Test
    void authorizationGithubUrl_InvalidAccount_GithubUrl() throws Exception {
        // given
        String githubAuthorizationGithubUrl = "http://github.authorization.url";
        given(oAuthService.getGithubAuthorizationUrl()).willReturn(githubAuthorizationGithubUrl);

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
        given(oAuthService.createToken(githubAuthorizationCode))
            .willReturn(new TokenDto("jwt token", "binghe"));

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
