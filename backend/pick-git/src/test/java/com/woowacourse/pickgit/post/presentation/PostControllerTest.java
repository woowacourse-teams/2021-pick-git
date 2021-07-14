package com.woowacourse.pickgit.post.presentation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.FileFactory;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.NestedServletException;

@WebMvcTest(PostController.class)
@ActiveProfiles("test")
class PostControllerTest {

    private static final String USERNAME = "dani";
    private static final String ACCESS_TOKEN = "pickgit";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private OAuthService oAuthService;

    private LoginUser user;
    private List<MultipartFile> images;
    private String githubRepoUrl;
    private String[] tags;
    private String content;

    @BeforeEach
    void setUp() {
        user = new LoginUser(USERNAME, ACCESS_TOKEN);
        images = List.of(
            FileFactory.getTestImage1(),
            FileFactory.getTestImage2()
        );
        githubRepoUrl = "https://github.com/woowacourse-teams/2021-pick-git/";
        tags = new String[]{"java", "spring"};
        content = "pickgit";
    }

    @DisplayName("게시물을 작성할 수 있다. - 사용자")
    @Test
    void write_LoginUser_Success() throws Exception {
        // given

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(user);
        given(postService.write(any(PostRequestDto.class)))
            .willReturn(new PostResponseDto(1L));

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("githubRepoUrl", githubRepoUrl);
        multiValueMap.add("content", content);

        // then
        mockMvc.perform(multipart("/api/posts")
            .file(FileFactory.getTestImage1())
            .file(FileFactory.getTestImage2())
            .params(multiValueMap)
            .param("tags", tags)
            .header(HttpHeaders.AUTHORIZATION, user.getAccessToken()))
            .andExpect(status().isCreated());
    }

    @DisplayName("게시물을 작성할 수 없다. - 게스트")
    @Test
    void write_GuestUser_Fail() throws Exception {
        // given
        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willCallRealMethod();

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("githubRepoUrl", githubRepoUrl);
        multiValueMap.add("content", content);

        // then
        assertThatCode(() ->
            mockMvc.perform(multipart("/api/posts")
                .file(FileFactory.getTestImage1())
                .file(FileFactory.getTestImage2())
                .params(multiValueMap)
                .param("tags", tags)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN))
                .andExpect(status().is4xxClientError())
        ).isInstanceOf(NestedServletException.class);

    }
}
