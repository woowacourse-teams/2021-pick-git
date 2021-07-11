package com.woowacourse.pickgit.post.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.PostResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.PostRequest;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PostController.class)
@ActiveProfiles("test")
class PostControllerTest {

    private static final String USERNAME = "dani";
    private static final String ACCESS_TOKEN = "pickgit";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private OAuthService oAuthService;

    private LoginUser user;
    private List<String> images;
    private String githubRepoUrl;
    private List<String> tags;
    private String content;

    @BeforeEach
    void setUp() {
        user = new LoginUser(USERNAME, ACCESS_TOKEN);
        images = List.of("image1", "image2");
        githubRepoUrl = "https://github.com/woowacourse-teams/2021-pick-git/";
        tags = List.of("java", "spring");
        content = "pickgit";
    }

    @DisplayName("사용자는 게시물을 작성할 수 있다.")
    @Test
    void writePost_LoginUser_Success() throws Exception {
        // given
        PostRequest request = new PostRequest(images, githubRepoUrl, tags, content);

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(user);
        given(postService.writePost(any(PostRequestDto.class)))
            .willReturn(new PostResponseDto(1L));

        // then
        mockMvc.perform(post("/api/posts")
            .header(HttpHeaders.AUTHORIZATION, user.getAccessToken())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @DisplayName("게스트는 게시물을 작성할 수 없다.")
    @Test
    void writePost_GuestUser_Fail() throws Exception {
        // given
        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willCallRealMethod();

        // then
        mockMvc.perform(post("/api/posts")
            .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN))
            .andExpect(status().is4xxClientError());
    }
}
