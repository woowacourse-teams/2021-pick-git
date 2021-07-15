package com.woowacourse.pickgit.post.presentation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.FileFactory;
import com.woowacourse.pickgit.config.StorageConfiguration;
import com.woowacourse.pickgit.post.application.CommentRequestDto;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoriesResponseDto;
import com.woowacourse.pickgit.post.application.dto.CommentDto;
import com.woowacourse.pickgit.post.domain.comment.CommentFormatException;
import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.NestedServletException;

@Import({StorageConfiguration.class})
@ExtendWith(SpringExtension.class)
@WebMvcTest(PostController.class)
@ActiveProfiles("test")
class PostControllerTest {

    private static final String USERNAME = "jipark3";
    private static final String ACCESS_TOKEN = "pickgit";
    private static final String API_ACCESS_TOKEN = "oauth.access.token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private OAuthService oAuthService;

    private LoginUser user;
    private List<MultipartFile> images;
    private String githubRepoUrl;
    private String[] tags;
    private String postContent;

    @BeforeEach
    void setUp() {
        user = new LoginUser(USERNAME, ACCESS_TOKEN);
        images = List.of(
            FileFactory.getTestImage1(),
            FileFactory.getTestImage2()
        );
        githubRepoUrl = "https://github.com/woowacourse-teams/2021-pick-git/";
        tags = new String[]{"java", "spring"};
        postContent = "pickgit";
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
        multiValueMap.add("content", postContent);

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
        multiValueMap.add("content", postContent);

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

    @DisplayName("특정 Post에 댓글을 추가한다.")
    @Test
    void addComment_ValidContent_Success() throws Exception {
        LoginUser loginUser = new LoginUser("kevin", "token");
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(loginUser);

        String url = "/api/posts/1/comments";
        CommentDto commentResponseDto =
            new CommentDto(1L, "kevin", "test comment", false);
        String requestBody = objectMapper.writeValueAsString("test comment");
        String responseBody = objectMapper.writeValueAsString(commentResponseDto);
        given(postService.addComment(any(CommentRequestDto.class)))
            .willReturn(commentResponseDto);

        addCommentApi(url, requestBody)
            .andExpect(status().isOk())
            .andExpect(content().string(responseBody));

        verify(postService, times(1)).addComment(any(CommentRequestDto.class));
    }

    private ResultActions addCommentApi(String url, String requestBody) throws Exception {
        return mockMvc.perform(post(url)
            .header("Authorization", "Bearer test")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody));
    }

    @DisplayName("특정 Post에 댓글 등록 실패한다. - 빈 댓글인 경우.")
    @Test
    void addComment_InValidContent_ExceptionThrown() throws Exception {
        LoginUser loginUser = new LoginUser("kevin", "token");
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(loginUser);

        String url = "/api/posts/1/comments";
        String requestBody = objectMapper.writeValueAsString("");
        given(postService.addComment(any(CommentRequestDto.class)))
            .willThrow(new CommentFormatException());

        addCommentApi(url, requestBody)
            .andExpect(status().isBadRequest())
            .andExpect(content().string("F0002"));

        verify(postService, times(1)).addComment(any(CommentRequestDto.class));
    }

    @DisplayName("사용자는 Repository 목록을 가져올 수 있다.")
    @Test
    void showRepositories_LoginUser_Success() throws Exception {
        // given
        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(user);

        RepositoryRequestDto requestDto =
            new RepositoryRequestDto(API_ACCESS_TOKEN, USERNAME);
        RepositoriesResponseDto responseDto = new RepositoriesResponseDto(List.of(
            new RepositoryResponseDto("pick"),
            new RepositoryResponseDto("git")
        ));
        String repositories = objectMapper.writeValueAsString(responseDto.getRepositories());

        given(postService.showRepositories(any(RepositoryRequestDto.class)))
            .willReturn(responseDto);

        // then
        mockMvc.perform(get("/api/github/" + USERNAME + "/repositories")
            .header(HttpHeaders.AUTHORIZATION, API_ACCESS_TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().string(repositories));
    }
}
