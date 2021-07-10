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
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class OAuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private OAuthController OAuthController;

    @MockBean
    private OAuthService oAuthService;

    @DisplayName("Github 로그인 요청을 하면 Github 인증 URL을 반환한다.")
    @Test
    void authorizationGithubUrl_InvalidAccount_GithubUrl() throws Exception {
        String githubAuthorizationGithubUrl = "http://github.authorization.url";
        when(oAuthService.getGithubAuthorizationUrl()).thenReturn(githubAuthorizationGithubUrl);

        mockMvc.perform(get("/authorization/github"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(githubAuthorizationGithubUrl));
    }
}
