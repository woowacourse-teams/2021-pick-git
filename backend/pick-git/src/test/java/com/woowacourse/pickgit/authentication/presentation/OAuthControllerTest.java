package com.woowacourse.pickgit.authentication.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.woowacourse.pickgit.authentication.application.OAuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

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
        mockMvc.perform(get("/api/authorization/github"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(githubAuthorizationGithubUrl));
    }

    @DisplayName("Github 로그인 인증 후 토큰을 발행하여 반환한다.")
    @Test
    void afterAuthorizeGithubLogin_ValidAccount_JWTToken() throws Exception {
        // given
        String githubAuthorizationCode = "random";
        when(oAuthService.createToken(githubAuthorizationCode)).thenReturn("jwt token");

        // when, then
        mockMvc.perform(get("/api/afterlogin?code=" + githubAuthorizationCode))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("jwt token"));
    }
}
